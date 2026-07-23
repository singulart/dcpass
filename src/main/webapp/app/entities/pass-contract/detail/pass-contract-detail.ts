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
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPassContract } from '../pass-contract.model';
import { IContractPaymentSummary, PassContractService } from '../service/pass-contract.service';

@Component({
  selector: 'jhi-pass-contract-detail',
  templateUrl: './pass-contract-detail.html',
  imports: [FontAwesomeModule, NgbModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe, FormatMediumDatePipe, CurrencyPipe],
})
export class PassContractDetail {
  passContract = input<IPassContract | null>(null);
  paymentSummary = signal<IContractPaymentSummary | null>(null);
  paymentSummaryLoading = signal(false);

  protected readonly accountService = inject(AccountService);
  protected readonly passContractService = inject(PassContractService);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    toObservable(this.passContract)
      .pipe(
        map(contract => contract?.contractNumber?.trim() || null),
        distinctUntilChanged(),
        tap(contractNumber => {
          this.paymentSummaryLoading.set(!!contractNumber);
          if (!contractNumber) {
            this.paymentSummary.set(null);
          }
        }),
        switchMap(contractNumber => {
          if (!contractNumber) {
            return EMPTY;
          }
          return this.passContractService.getPaymentSummary(contractNumber).pipe(
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
