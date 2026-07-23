import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { IPassPayment } from '../pass-payment.model';
import { PassPaymentService } from '../service/pass-payment.service';

import { PassPaymentFormGroup, PassPaymentFormService } from './pass-payment-form.service';

@Component({
  selector: 'jhi-pass-payment-update',
  templateUrl: './pass-payment-update.html',
  imports: [NgbModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PassPaymentUpdate implements OnInit {
  isSaving = signal(false);
  passPayment: IPassPayment | null = null;

  protected passPaymentService = inject(PassPaymentService);
  protected passPaymentFormService = inject(PassPaymentFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PassPaymentFormGroup = this.passPaymentFormService.createPassPaymentFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ passPayment }) => {
      this.passPayment = passPayment;
      if (passPayment) {
        this.updateForm(passPayment);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const passPayment = this.passPaymentFormService.getPassPayment(this.editForm);
    if (passPayment.id === null) {
      this.subscribeToSaveResponse(this.passPaymentService.create(passPayment));
    } else {
      this.subscribeToSaveResponse(this.passPaymentService.update(passPayment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPassPayment>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(passPayment: IPassPayment): void {
    this.passPayment = passPayment;
    this.passPaymentFormService.resetForm(this.editForm, passPayment);
  }
}
