import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPassPayment } from '../pass-payment.model';
import { PassPaymentService } from '../service/pass-payment.service';

@Component({
  templateUrl: './pass-payment-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class PassPaymentDeleteDialog {
  passPayment?: IPassPayment;

  protected passPaymentService = inject(PassPaymentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.passPaymentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
