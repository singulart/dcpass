import { CurrencyPipe } from '@angular/common';
import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import dayjs from 'dayjs/esm';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subscription, combineLatest, distinctUntilChanged, filter, finalize, map, switchMap, tap } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SEARCH_QUERY_PARAM, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { Filter, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { PassPaymentDeleteDialog } from '../delete/pass-payment-delete-dialog';
import { IPassPayment } from '../pass-payment.model';
import { EntityArrayResponseType, PassPaymentService } from '../service/pass-payment.service';

@Component({
  selector: 'jhi-pass-payment',
  templateUrl: './pass-payment.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    NgbModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    FormatMediumDatetimePipe,
    CurrencyPipe,
    Filter,
    ItemCount,
  ],
})
export class PassPayment implements OnInit {
  subscription: Subscription | null = null;
  passPayments = signal<IPassPayment[]>([]);
  isLoading = signal(false);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();
  searchInput = '';

  advancedSearchCollapsed = true;
  paymentDateFrom: dayjs.Dayjs | null = null;
  paymentDateTo: dayjs.Dayjs | null = null;

  itemsPerPage = signal(ITEMS_PER_PAGE);
  totalItems = signal(0);
  page = signal(1);

  readonly router = inject(Router);
  protected readonly accountService = inject(AccountService);
  protected readonly passPaymentService = inject(PassPaymentService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  trackId = (item: IPassPayment): number => this.passPaymentService.getPassPaymentIdentifier(item);

  paymentDateRangeValidationError(): boolean {
    if (this.paymentDateFrom && this.paymentDateTo) {
      return this.paymentDateTo.isBefore(this.paymentDateFrom) || this.paymentDateTo.isSame(this.paymentDateFrom);
    }
    return false;
  }

  ngOnInit(): void {
    // switchMap cancels an in-flight list request when route params change (sort, search, page).
    // Query params are read from the route snapshot inside switchMap so each response matches its URL.
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        map(([params, data]) => ({
          params,
          data,
          requestKey: params.keys
            .slice()
            .sort()
            .map(key => `${key}=${params.getAll(key).join(',')}`)
            .join('&'),
        })),
        distinctUntilChanged((previous, current) => previous.requestKey === current.requestKey),
        tap(({ params, data }) => this.fillComponentAttributeFromRoute(params, data)),
        switchMap(({ params, data }) => this.queryBackend(params, data)),
      )
      .subscribe(res => this.onResponseSuccess(res));

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  delete(passPayment: IPassPayment): void {
    const modalRef = this.modalService.open(PassPaymentDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.passPayment = passPayment;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend(this.activatedRoute.snapshot.queryParamMap, this.activatedRoute.snapshot.data).subscribe(
      (res: EntityArrayResponseType) => this.onResponseSuccess(res),
    );
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  onSearch(): void {
    if (this.paymentDateRangeValidationError()) {
      return;
    }
    this.handleNavigation(1, this.sortState(), this.filters.filterOptions, this.searchInput);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
    this.searchInput = params.get(SEARCH_QUERY_PARAM) ?? '';
    const paymentDateFromParam = params.get('paymentDate.greaterThanOrEqual');
    const paymentDateToParam = params.get('paymentDate.lessThanOrEqual');
    this.paymentDateFrom = paymentDateFromParam && dayjs(paymentDateFromParam).isValid() ? dayjs(paymentDateFromParam) : null;
    this.paymentDateTo = paymentDateToParam && dayjs(paymentDateToParam).isValid() ? dayjs(paymentDateToParam) : null;

    if (this.paymentDateFrom || this.paymentDateTo) {
      this.advancedSearchCollapsed = false;
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.passPayments.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IPassPayment[] | null): IPassPayment[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
  }

  protected queryBackend(params: ParamMap, data: Data): Observable<EntityArrayResponseType> {
    this.isLoading.set(true);
    const pageToLoad = +(params.get(PAGE_HEADER) ?? 1);
    const sortState = this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]);
    const searchInput = params.get(SEARCH_QUERY_PARAM) ?? '';
    const paymentDateFromParam = params.get('paymentDate.greaterThanOrEqual');
    const paymentDateToParam = params.get('paymentDate.lessThanOrEqual');
    const paymentDateFrom = paymentDateFromParam && dayjs(paymentDateFromParam).isValid() ? dayjs(paymentDateFromParam) : null;
    const paymentDateTo = paymentDateToParam && dayjs(paymentDateToParam).isValid() ? dayjs(paymentDateToParam) : null;
    const paymentDateRangeInvalid =
      !!paymentDateFrom && !!paymentDateTo && (paymentDateTo.isBefore(paymentDateFrom) || paymentDateTo.isSame(paymentDateFrom));

    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };
    if (searchInput.trim()) {
      queryObject[SEARCH_QUERY_PARAM] = searchInput.trim();
    }
    if (!paymentDateRangeInvalid) {
      if (paymentDateFrom?.isValid()) {
        queryObject['paymentDate.greaterThanOrEqual'] = paymentDateFrom.startOf('day').toJSON();
      }
      if (paymentDateTo?.isValid()) {
        queryObject['paymentDate.lessThanOrEqual'] = paymentDateTo.endOf('day').toJSON();
      }
    }
    for (const filterOption of this.filters.filterOptions) {
      queryObject[filterOption.name] = filterOption.values;
    }
    return this.passPaymentService.query(queryObject).pipe(finalize(() => this.isLoading.set(false)));
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[], searchOverride?: string): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    const searchValue = searchOverride ?? this.searchInput;
    if (searchValue.trim()) {
      queryParamsObj[SEARCH_QUERY_PARAM] = searchValue.trim();
    }

    if (!this.paymentDateRangeValidationError()) {
      if (this.paymentDateFrom?.isValid()) {
        queryParamsObj['paymentDate.greaterThanOrEqual'] = this.paymentDateFrom.startOf('day').toJSON();
      }
      if (this.paymentDateTo?.isValid()) {
        queryParamsObj['paymentDate.lessThanOrEqual'] = this.paymentDateTo.endOf('day').toJSON();
      }
    }

    if (filterOptions) {
      for (const filterOption of filterOptions) {
        queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
      }
    }

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
