import { CurrencyPipe } from '@angular/common';
import { Component, DestroyRef, inject, input, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { EMPTY, catchError, distinctUntilChanged, map, switchMap, tap } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { PassContractService } from '../../pass-contract/service/pass-contract.service';
import { IPurchaseOrder } from '../purchase-order.model';
import { IPurchaseOrderPaymentSummary, PurchaseOrderService } from '../service/purchase-order.service';

@Component({
  selector: 'jhi-purchase-order-detail',
  templateUrl: './purchase-order-detail.html',
  imports: [FontAwesomeModule, NgbModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe, CurrencyPipe],
})
export class PurchaseOrderDetail {
  purchaseOrder = input<IPurchaseOrder | null>(null);
  passContractId = signal<number | null>(null);
  paymentSummary = signal<IPurchaseOrderPaymentSummary | null>(null);
  paymentSummaryLoading = signal(false);

  protected readonly accountService = inject(AccountService);
  private readonly passContractService = inject(PassContractService);
  private readonly purchaseOrderService = inject(PurchaseOrderService);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    toObservable(this.purchaseOrder)
      .pipe(
        map(purchaseOrder => purchaseOrder?.contractNumber?.trim() ?? null),
        map(contractNumber => (contractNumber === '' ? null : contractNumber)),
        distinctUntilChanged(),
        tap(() => this.passContractId.set(null)),
        switchMap(contractNumber => {
          if (!contractNumber) {
            return EMPTY;
          }

          return this.passContractService.query({ 'contractNumber.equals': contractNumber, size: 1 }).pipe(
            tap(response => this.passContractId.set(response.body?.[0]?.id ?? null)),
            catchError(() => EMPTY),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();

    toObservable(this.purchaseOrder)
      .pipe(
        map(purchaseOrder => purchaseOrder?.poNumber?.trim() ?? null),
        map(poNumber => (poNumber === '' ? null : poNumber)),
        distinctUntilChanged(),
        tap(poNumber => {
          this.paymentSummaryLoading.set(!!poNumber);
          if (!poNumber) {
            this.paymentSummary.set(null);
          }
        }),
        switchMap(poNumber => {
          if (!poNumber) {
            return EMPTY;
          }
          return this.purchaseOrderService.getPaymentSummary(poNumber).pipe(
            tap({
              next: res => {
                this.paymentSummary.set(res.body ?? null);
                this.paymentSummaryLoading.set(false);
              },
            }),
            catchError(() => {
              this.paymentSummary.set(null);
              this.paymentSummaryLoading.set(false);
              return EMPTY;
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();
  }

  previousState(): void {
    globalThis.history.back();
  }
}
