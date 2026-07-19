import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AccountService } from 'app/core/auth/account.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IPurchaseOrder } from '../purchase-order.model';

@Component({
  selector: 'jhi-purchase-order-detail',
  templateUrl: './purchase-order-detail.html',
  imports: [FontAwesomeModule, NgbModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class PurchaseOrderDetail {
  purchaseOrder = input<IPurchaseOrder | null>(null);
  protected readonly accountService = inject(AccountService);

  previousState(): void {
    globalThis.history.back();
  }
}
