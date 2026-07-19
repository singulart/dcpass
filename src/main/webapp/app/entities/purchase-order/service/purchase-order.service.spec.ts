import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPurchaseOrder } from '../purchase-order.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../purchase-order.test-samples';

import { PurchaseOrderService, RestPurchaseOrder } from './purchase-order.service';

const requireRestSample: RestPurchaseOrder = {
  ...sampleWithRequiredData,
  orderedDate: sampleWithRequiredData.orderedDate?.toJSON(),
  createDate: sampleWithRequiredData.createDate?.toJSON(),
  dcsLastModDttm: sampleWithRequiredData.dcsLastModDttm?.toJSON(),
  dcsRecCrtDttm: sampleWithRequiredData.dcsRecCrtDttm?.toJSON(),
};

describe('PurchaseOrder Service', () => {
  let service: PurchaseOrderService;
  let httpMock: HttpTestingController;
  let expectedResult: IPurchaseOrder | IPurchaseOrder[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PurchaseOrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a PurchaseOrder', () => {
      const purchaseOrder = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(purchaseOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PurchaseOrder', () => {
      const purchaseOrder = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(purchaseOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PurchaseOrder', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PurchaseOrder', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPurchaseOrderToCollectionIfMissing', () => {
      it('should add a PurchaseOrder to an empty array', () => {
        const purchaseOrder: IPurchaseOrder = sampleWithRequiredData;
        expectedResult = service.addPurchaseOrderToCollectionIfMissing([], purchaseOrder);
        expect(expectedResult).toEqual([purchaseOrder]);
      });

      it('should not add a PurchaseOrder to an array that contains it', () => {
        const purchaseOrder: IPurchaseOrder = sampleWithRequiredData;
        const purchaseOrderCollection: IPurchaseOrder[] = [{ ...purchaseOrder }, sampleWithPartialData];
        expectedResult = service.addPurchaseOrderToCollectionIfMissing(purchaseOrderCollection, purchaseOrder);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PurchaseOrder to an array that doesn't contain it", () => {
        const purchaseOrder: IPurchaseOrder = sampleWithRequiredData;
        const purchaseOrderCollection: IPurchaseOrder[] = [sampleWithPartialData];
        expectedResult = service.addPurchaseOrderToCollectionIfMissing(purchaseOrderCollection, purchaseOrder);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchaseOrder);
      });

      it('should add only unique PurchaseOrder to an array', () => {
        const purchaseOrderArray: IPurchaseOrder[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const purchaseOrderCollection: IPurchaseOrder[] = [sampleWithRequiredData];
        expectedResult = service.addPurchaseOrderToCollectionIfMissing(purchaseOrderCollection, ...purchaseOrderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const purchaseOrder: IPurchaseOrder = sampleWithRequiredData;
        const purchaseOrder2: IPurchaseOrder = sampleWithPartialData;
        expectedResult = service.addPurchaseOrderToCollectionIfMissing([], purchaseOrder, purchaseOrder2);
        expect(expectedResult).toEqual([purchaseOrder, purchaseOrder2]);
      });

      it('should accept null and undefined values', () => {
        const purchaseOrder: IPurchaseOrder = sampleWithRequiredData;
        expectedResult = service.addPurchaseOrderToCollectionIfMissing([], null, purchaseOrder, undefined);
        expect(expectedResult).toEqual([purchaseOrder]);
      });

      it('should return initial array if no PurchaseOrder is added', () => {
        const purchaseOrderCollection: IPurchaseOrder[] = [sampleWithRequiredData];
        expectedResult = service.addPurchaseOrderToCollectionIfMissing(purchaseOrderCollection, undefined, null);
        expect(expectedResult).toEqual(purchaseOrderCollection);
      });
    });

    describe('comparePurchaseOrder', () => {
      it('should return true if both entities are null', () => {
        expect(service.comparePurchaseOrder(null, null)).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        expect(service.comparePurchaseOrder({ id: 13513 }, null)).toEqual(false);
        expect(service.comparePurchaseOrder(null, { id: 13513 })).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        expect(service.comparePurchaseOrder({ id: 13513 }, { id: 3727 })).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        expect(service.comparePurchaseOrder({ id: 13513 }, { id: 13513 })).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
