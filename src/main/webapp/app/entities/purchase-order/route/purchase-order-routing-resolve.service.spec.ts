import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { IPurchaseOrder } from '../purchase-order.model';
import { PurchaseOrderService } from '../service/purchase-order.service';

import purchaseOrderResolve from './purchase-order-routing-resolve.service';

describe('PurchaseOrder routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: PurchaseOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    vitest.spyOn(mockRouter, 'navigate');
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(PurchaseOrderService);
  });

  describe('resolve', () => {
    it('should return IPurchaseOrder returned by find', async () => {
      service.find = vitest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      await new Promise<void>(resolve => {
        TestBed.runInInjectionContext(() => {
          purchaseOrderResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              expect(service.find).toHaveBeenCalledWith(123);
              expect(result).toEqual({ id: 123 });
              resolve();
            },
          });
        });
      });
    });

    it('should return null if id is not provided', async () => {
      service.find = vitest.fn();
      mockActivatedRouteSnapshot.params = {};

      await new Promise<void>(resolve => {
        TestBed.runInInjectionContext(() => {
          purchaseOrderResolve(mockActivatedRouteSnapshot).subscribe({
            next(result) {
              expect(service.find).not.toHaveBeenCalled();
              expect(result).toEqual(null);
              resolve();
            },
          });
        });
      });
    });

    it('should route to 404 page if data not found in server', async () => {
      vitest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IPurchaseOrder>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      await new Promise<void>(resolve => {
        TestBed.runInInjectionContext(() => {
          purchaseOrderResolve(mockActivatedRouteSnapshot).subscribe({
            complete() {
              expect(service.find).toHaveBeenCalledWith(123);
              expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
              resolve();
            },
          });
        });
      });
    });
  });
});
