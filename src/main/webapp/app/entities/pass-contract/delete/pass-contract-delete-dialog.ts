import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPassContract } from '../pass-contract.model';
import { PassContractService } from '../service/pass-contract.service';

@Component({
  templateUrl: './pass-contract-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class PassContractDeleteDialog {
  passContract?: IPassContract;

  protected passContractService = inject(PassContractService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.passContractService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
