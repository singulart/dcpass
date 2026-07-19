import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { IPurchaseOrder } from '../purchase-order.model';
import { PurchaseOrderService } from '../service/purchase-order.service';

import { PurchaseOrderFormGroup, PurchaseOrderFormService } from './purchase-order-form.service';

@Component({
  selector: 'jhi-purchase-order-update',
  templateUrl: './purchase-order-update.html',
  imports: [NgbModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PurchaseOrderUpdate implements OnInit {
  isSaving = signal(false);
  purchaseOrder: IPurchaseOrder | null = null;

  protected purchaseOrderService = inject(PurchaseOrderService);
  protected purchaseOrderFormService = inject(PurchaseOrderFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PurchaseOrderFormGroup = this.purchaseOrderFormService.createPurchaseOrderFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchaseOrder }) => {
      this.purchaseOrder = purchaseOrder;
      if (purchaseOrder) {
        this.updateForm(purchaseOrder);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const purchaseOrder = this.purchaseOrderFormService.getPurchaseOrder(this.editForm);
    if (purchaseOrder.id === null) {
      this.subscribeToSaveResponse(this.purchaseOrderService.create(purchaseOrder));
    } else {
      this.subscribeToSaveResponse(this.purchaseOrderService.update(purchaseOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchaseOrder>>): void {
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

  protected updateForm(purchaseOrder: IPurchaseOrder): void {
    this.purchaseOrder = purchaseOrder;
    this.purchaseOrderFormService.resetForm(this.editForm, purchaseOrder);
  }
}
