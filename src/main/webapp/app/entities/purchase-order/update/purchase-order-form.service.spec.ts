import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../purchase-order.test-samples';

import { PurchaseOrderFormService } from './purchase-order-form.service';

describe('PurchaseOrder Form Service', () => {
  let service: PurchaseOrderFormService;

  beforeEach(() => {
    service = TestBed.inject(PurchaseOrderFormService);
  });

  describe('Service methods', () => {
    describe('createPurchaseOrderFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPurchaseOrderFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            poNumber: expect.any(Object),
            agencyCode: expect.any(Object),
            status: expect.any(Object),
            requester: expect.any(Object),
            requisitionNumber: expect.any(Object),
            commodityCode: expect.any(Object),
            commodityName: expect.any(Object),
            contractNumber: expect.any(Object),
            supplier: expect.any(Object),
            orderedDate: expect.any(Object),
            createDate: expect.any(Object),
            poTotal: expect.any(Object),
            fiscalYear: expect.any(Object),
            poTitle: expect.any(Object),
            agencyAcronym: expect.any(Object),
            agencyName: expect.any(Object),
            dcsLastModDttm: expect.any(Object),
            dcsRecCrtDttm: expect.any(Object),
            objectId: expect.any(Object),
          }),
        );
      });

      it('passing IPurchaseOrder should create a new form with FormGroup', () => {
        const formGroup = service.createPurchaseOrderFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            poNumber: expect.any(Object),
            agencyCode: expect.any(Object),
            status: expect.any(Object),
            requester: expect.any(Object),
            requisitionNumber: expect.any(Object),
            commodityCode: expect.any(Object),
            commodityName: expect.any(Object),
            contractNumber: expect.any(Object),
            supplier: expect.any(Object),
            orderedDate: expect.any(Object),
            createDate: expect.any(Object),
            poTotal: expect.any(Object),
            fiscalYear: expect.any(Object),
            poTitle: expect.any(Object),
            agencyAcronym: expect.any(Object),
            agencyName: expect.any(Object),
            dcsLastModDttm: expect.any(Object),
            dcsRecCrtDttm: expect.any(Object),
            objectId: expect.any(Object),
          }),
        );
      });
    });

    describe('getPurchaseOrder', () => {
      it('should return NewPurchaseOrder for default PurchaseOrder initial value', () => {
        const formGroup = service.createPurchaseOrderFormGroup(sampleWithNewData);
        const purchaseOrder = service.getPurchaseOrder(formGroup);
        expect(purchaseOrder).toMatchObject(sampleWithNewData);
      });

      it('should return NewPurchaseOrder for empty PurchaseOrder initial value', () => {
        const formGroup = service.createPurchaseOrderFormGroup();
        const purchaseOrder = service.getPurchaseOrder(formGroup);
        expect(purchaseOrder).toMatchObject({});
      });

      it('should return IPurchaseOrder', () => {
        const formGroup = service.createPurchaseOrderFormGroup(sampleWithRequiredData);
        const purchaseOrder = service.getPurchaseOrder(formGroup);
        expect(purchaseOrder).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPurchaseOrder should not enable id FormControl', () => {
        const formGroup = service.createPurchaseOrderFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPurchaseOrder should disable id FormControl', () => {
        const formGroup = service.createPurchaseOrderFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
