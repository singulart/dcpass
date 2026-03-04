import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { IPassContract } from '../pass-contract.model';
import { PassContractService } from '../service/pass-contract.service';

import { PassContractFormGroup, PassContractFormService } from './pass-contract-form.service';

@Component({
  selector: 'jhi-pass-contract-update',
  templateUrl: './pass-contract-update.html',
  imports: [NgbModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PassContractUpdate implements OnInit {
  isSaving = signal(false);
  passContract: IPassContract | null = null;

  protected passContractService = inject(PassContractService);
  protected passContractFormService = inject(PassContractFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PassContractFormGroup = this.passContractFormService.createPassContractFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ passContract }) => {
      this.passContract = passContract;
      if (passContract) {
        this.updateForm(passContract);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const passContract = this.passContractFormService.getPassContract(this.editForm);
    if (passContract.id === null) {
      this.subscribeToSaveResponse(this.passContractService.create(passContract));
    } else {
      this.subscribeToSaveResponse(this.passContractService.update(passContract));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPassContract>>): void {
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

  protected updateForm(passContract: IPassContract): void {
    this.passContract = passContract;
    this.passContractFormService.resetForm(this.editForm, passContract);
  }
}
