import { CurrencyPipe } from '@angular/common';
import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import dayjs from 'dayjs/esm';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal, NgbModule, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subscription, combineLatest, filter, finalize, tap } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { DATE_FORMAT } from 'app/config/input.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SEARCH_QUERY_PARAM, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { Filter, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { PassContractDeleteDialog } from '../delete/pass-contract-delete-dialog';
import { IPassContract } from '../pass-contract.model';
import { EntityArrayResponseType, PassContractService } from '../service/pass-contract.service';

@Component({
  selector: 'jhi-pass-contract',
  templateUrl: './pass-contract.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    NgbModule,
    NgbTooltip,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    CurrencyPipe,
    Filter,
    ItemCount,
  ],
})
export class PassContract implements OnInit {
  subscription: Subscription | null = null;
  passContracts = signal<IPassContract[]>([]);
  isLoading = signal(false);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();
  searchInput = '';

  advancedSearchCollapsed = true;
  awardDateFrom: dayjs.Dayjs | null = null;
  awardDateTo: dayjs.Dayjs | null = null;
  startDateFrom: dayjs.Dayjs | null = null;
  startDateTo: dayjs.Dayjs | null = null;
  endDateFrom: dayjs.Dayjs | null = null;
  endDateTo: dayjs.Dayjs | null = null;

  itemsPerPage = signal(ITEMS_PER_PAGE);
  totalItems = signal(0);
  page = signal(1);

  readonly router = inject(Router);
  protected readonly accountService = inject(AccountService);
  protected readonly passContractService = inject(PassContractService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  trackId = (item: IPassContract): number => this.passContractService.getPassContractIdentifier(item);

  awardDateRangeValidationError(): boolean {
    if (this.awardDateFrom && this.awardDateTo) {
      return this.awardDateTo.isBefore(this.awardDateFrom) || this.awardDateTo.isSame(this.awardDateFrom);
    }
    return false;
  }

  startDateRangeValidationError(): boolean {
    if (this.startDateFrom && this.startDateTo) {
      return this.startDateTo.isBefore(this.startDateFrom) || this.startDateTo.isSame(this.startDateFrom);
    }
    return false;
  }

  endDateRangeValidationError(): boolean {
    if (this.endDateFrom && this.endDateTo) {
      return this.endDateTo.isBefore(this.endDateFrom) || this.endDateTo.isSame(this.endDateFrom);
    }
    return false;
  }

  dateRangeValidationError(): boolean {
    return this.awardDateRangeValidationError() || this.startDateRangeValidationError() || this.endDateRangeValidationError();
  }

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  delete(passContract: IPassContract): void {
    const modalRef = this.modalService.open(PassContractDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.passContract = passContract;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe((res: EntityArrayResponseType) => this.onResponseSuccess(res));
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
    this.searchInput = params.get(SEARCH_QUERY_PARAM) ?? '';
    const awardDateFromParam = params.get('awardDate.greaterThanOrEqual');
    const awardDateToParam = params.get('awardDate.lessThanOrEqual');
    this.awardDateFrom = awardDateFromParam && dayjs(awardDateFromParam).isValid() ? dayjs(awardDateFromParam) : null;
    this.awardDateTo = awardDateToParam && dayjs(awardDateToParam).isValid() ? dayjs(awardDateToParam) : null;

    const startDateFromParam = params.get('startDate.greaterThanOrEqual');
    const startDateToParam = params.get('startDate.lessThanOrEqual');
    this.startDateFrom = startDateFromParam && dayjs(startDateFromParam).isValid() ? dayjs(startDateFromParam) : null;
    this.startDateTo = startDateToParam && dayjs(startDateToParam).isValid() ? dayjs(startDateToParam) : null;

    const endDateFromParam = params.get('endDate.greaterThanOrEqual');
    const endDateToParam = params.get('endDate.lessThanOrEqual');
    this.endDateFrom = endDateFromParam && dayjs(endDateFromParam).isValid() ? dayjs(endDateFromParam) : null;
    this.endDateTo = endDateToParam && dayjs(endDateToParam).isValid() ? dayjs(endDateToParam) : null;

    if (this.awardDateFrom || this.awardDateTo || this.startDateFrom || this.startDateTo || this.endDateFrom || this.endDateTo) {
      this.advancedSearchCollapsed = false;
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.passContracts.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IPassContract[] | null): IPassContract[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading.set(true);
    const pageToLoad: number = this.page();
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    if (this.searchInput?.trim()) {
      queryObject[SEARCH_QUERY_PARAM] = this.searchInput.trim();
    }
    if (!this.dateRangeValidationError()) {
      if (this.awardDateFrom?.isValid()) {
        queryObject['awardDate.greaterThanOrEqual'] = this.awardDateFrom.format(DATE_FORMAT);
      }
      if (this.awardDateTo?.isValid()) {
        queryObject['awardDate.lessThanOrEqual'] = this.awardDateTo.format(DATE_FORMAT);
      }
      if (this.startDateFrom?.isValid()) {
        queryObject['startDate.greaterThanOrEqual'] = this.startDateFrom.format(DATE_FORMAT);
      }
      if (this.startDateTo?.isValid()) {
        queryObject['startDate.lessThanOrEqual'] = this.startDateTo.format(DATE_FORMAT);
      }
      if (this.endDateFrom?.isValid()) {
        queryObject['endDate.greaterThanOrEqual'] = this.endDateFrom.format(DATE_FORMAT);
      }
      if (this.endDateTo?.isValid()) {
        queryObject['endDate.lessThanOrEqual'] = this.endDateTo.format(DATE_FORMAT);
      }
    }
    for (const filterOption of this.filters.filterOptions) {
      queryObject[filterOption.name] = filterOption.values;
    }
    return this.passContractService.query(queryObject).pipe(finalize(() => this.isLoading.set(false)));
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[], searchOverride?: string): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    const searchValue = searchOverride !== undefined ? searchOverride : this.searchInput;
    if (searchValue?.trim()) {
      queryParamsObj[SEARCH_QUERY_PARAM] = searchValue.trim();
    }

    if (!this.dateRangeValidationError()) {
      if (this.awardDateFrom?.isValid()) {
        queryParamsObj['awardDate.greaterThanOrEqual'] = this.awardDateFrom.format(DATE_FORMAT);
      }
      if (this.awardDateTo?.isValid()) {
        queryParamsObj['awardDate.lessThanOrEqual'] = this.awardDateTo.format(DATE_FORMAT);
      }
      if (this.startDateFrom?.isValid()) {
        queryParamsObj['startDate.greaterThanOrEqual'] = this.startDateFrom.format(DATE_FORMAT);
      }
      if (this.startDateTo?.isValid()) {
        queryParamsObj['startDate.lessThanOrEqual'] = this.startDateTo.format(DATE_FORMAT);
      }
      if (this.endDateFrom?.isValid()) {
        queryParamsObj['endDate.greaterThanOrEqual'] = this.endDateFrom.format(DATE_FORMAT);
      }
      if (this.endDateTo?.isValid()) {
        queryParamsObj['endDate.lessThanOrEqual'] = this.endDateTo.format(DATE_FORMAT);
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

  onSearch(): void {
    if (this.dateRangeValidationError()) {
      return;
    }
    this.handleNavigation(1, this.sortState(), this.filters.filterOptions, this.searchInput);
  }
}
