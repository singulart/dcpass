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
import { PurchaseOrderDeleteDialog } from '../delete/purchase-order-delete-dialog';
import { IPurchaseOrder } from '../purchase-order.model';
import { EntityArrayResponseType, PurchaseOrderService } from '../service/purchase-order.service';

@Component({
  selector: 'jhi-purchase-order',
  templateUrl: './purchase-order.html',
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
export class PurchaseOrder implements OnInit {
  subscription: Subscription | null = null;
  purchaseOrders = signal<IPurchaseOrder[]>([]);
  isLoading = signal(false);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();
  searchInput = '';

  advancedSearchCollapsed = true;
  orderedDateFrom: dayjs.Dayjs | null = null;
  orderedDateTo: dayjs.Dayjs | null = null;

  itemsPerPage = signal(ITEMS_PER_PAGE);
  totalItems = signal(0);
  page = signal(1);

  readonly router = inject(Router);
  protected readonly accountService = inject(AccountService);
  protected readonly purchaseOrderService = inject(PurchaseOrderService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  trackId = (item: IPurchaseOrder): number => this.purchaseOrderService.getPurchaseOrderIdentifier(item);

  /**
   * Table display: NIGP numeric code only (7 digits), no description text.
   */
  formatCommodityCode(code: string | null | undefined): string {
    if (!code) {
      return '';
    }
    const digits = code.replace(/\D/g, '');
    return digits.length > 7 ? digits.slice(0, 7) : digits;
  }

  orderedDateRangeValidationError(): boolean {
    if (this.orderedDateFrom && this.orderedDateTo) {
      return this.orderedDateTo.isBefore(this.orderedDateFrom) || this.orderedDateTo.isSame(this.orderedDateFrom);
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

  delete(purchaseOrder: IPurchaseOrder): void {
    const modalRef = this.modalService.open(PurchaseOrderDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.purchaseOrder = purchaseOrder;
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
    if (this.orderedDateRangeValidationError()) {
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
    const orderedDateFromParam = params.get('orderedDate.greaterThanOrEqual');
    const orderedDateToParam = params.get('orderedDate.lessThanOrEqual');
    this.orderedDateFrom = orderedDateFromParam && dayjs(orderedDateFromParam).isValid() ? dayjs(orderedDateFromParam) : null;
    this.orderedDateTo = orderedDateToParam && dayjs(orderedDateToParam).isValid() ? dayjs(orderedDateToParam) : null;

    if (this.orderedDateFrom || this.orderedDateTo) {
      this.advancedSearchCollapsed = false;
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.purchaseOrders.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IPurchaseOrder[] | null): IPurchaseOrder[] {
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
    const orderedDateFromParam = params.get('orderedDate.greaterThanOrEqual');
    const orderedDateToParam = params.get('orderedDate.lessThanOrEqual');
    const orderedDateFrom = orderedDateFromParam && dayjs(orderedDateFromParam).isValid() ? dayjs(orderedDateFromParam) : null;
    const orderedDateTo = orderedDateToParam && dayjs(orderedDateToParam).isValid() ? dayjs(orderedDateToParam) : null;
    const orderedDateRangeInvalid =
      !!orderedDateFrom && !!orderedDateTo && (orderedDateTo.isBefore(orderedDateFrom) || orderedDateTo.isSame(orderedDateFrom));

    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };
    if (searchInput.trim()) {
      queryObject[SEARCH_QUERY_PARAM] = searchInput.trim();
    }
    if (!orderedDateRangeInvalid) {
      if (orderedDateFrom?.isValid()) {
        queryObject['orderedDate.greaterThanOrEqual'] = orderedDateFrom.startOf('day').toJSON();
      }
      if (orderedDateTo?.isValid()) {
        queryObject['orderedDate.lessThanOrEqual'] = orderedDateTo.endOf('day').toJSON();
      }
    }
    for (const filterOption of this.filters.filterOptions) {
      queryObject[filterOption.name] = filterOption.values;
    }
    return this.purchaseOrderService.query(queryObject).pipe(finalize(() => this.isLoading.set(false)));
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

    if (!this.orderedDateRangeValidationError()) {
      if (this.orderedDateFrom?.isValid()) {
        queryParamsObj['orderedDate.greaterThanOrEqual'] = this.orderedDateFrom.startOf('day').toJSON();
      }
      if (this.orderedDateTo?.isValid()) {
        queryParamsObj['orderedDate.lessThanOrEqual'] = this.orderedDateTo.endOf('day').toJSON();
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
