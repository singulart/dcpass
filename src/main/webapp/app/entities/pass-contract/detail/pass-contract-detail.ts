import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPassContract } from '../pass-contract.model';

@Component({
  selector: 'jhi-pass-contract-detail',
  templateUrl: './pass-contract-detail.html',
  imports: [FontAwesomeModule, NgbModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PassContractDetail {
  passContract = input<IPassContract | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
