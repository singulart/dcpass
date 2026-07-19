import { MockInstance, beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes } from '@fortawesome/free-solid-svg-icons';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject, of } from 'rxjs';

import { sampleWithRequiredData } from '../purchase-order.test-samples';
import { PurchaseOrderService } from '../service/purchase-order.service';

import { PurchaseOrder } from './purchase-order';

describe('PurchaseOrder Management Component', () => {
  let comp: PurchaseOrder;
  let fixture: ComponentFixture<PurchaseOrder>;
  let service: PurchaseOrderService;
  let routerNavigateSpy: MockInstance;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: {
              queryParams: {},
              queryParamMap: convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            },
          },
        },
      ],
    });

    fixture = TestBed.createComponent(PurchaseOrder);
    comp = fixture.componentInstance;
    service = TestBed.inject(PurchaseOrderService);
    routerNavigateSpy = vitest.spyOn(comp.router, 'navigate');

    vitest
      .spyOn(service, 'query')
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ id: 13513 }],
            headers: new HttpHeaders({
              link: '<http://localhost/api/foo?page=1&size=20>; rel="next"',
            }),
          }),
        ),
      )
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ id: 3727 }],
            headers: new HttpHeaders({
              link: '<http://localhost/api/foo?page=0&size=20>; rel="prev",<http://localhost/api/foo?page=2&size=20>; rel="next"',
            }),
          }),
        ),
      );

    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes);
  });

  it('should call load all on init', () => {
    comp.ngOnInit();

    expect(service.query).toHaveBeenCalled();
    expect(comp.purchaseOrders()[0]).toEqual(expect.objectContaining({ id: 13513 }));
  });

  describe('trackId', () => {
    it('should forward to purchaseOrderService', () => {
      const entity = { id: 13513 };
      vitest.spyOn(service, 'getPurchaseOrderIdentifier');
      const id = comp.trackId(entity);
      expect(service.getPurchaseOrderIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    comp.navigateToWithComponentValues({ predicate: 'non-existing-column', order: 'asc' });

    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['non-existing-column,asc'],
        }),
      }),
    );
  });

  it('should load a page', () => {
    comp.navigateToPage(1);

    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    comp.ngOnInit();

    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['id,desc'] }));
  });

  it('should calculate the filter attribute', () => {
    comp.ngOnInit();

    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
  });

  describe('delete', () => {
    let ngbModal: NgbModal;
    let deleteModalMock: any;

    beforeEach(() => {
      deleteModalMock = { componentInstance: {}, closed: new Subject() };
      ngbModal = (comp as any).modalService;
      vitest.spyOn(ngbModal, 'open').mockReturnValue(deleteModalMock);
    });

    it('on confirm should call load', inject([], () => {
      vitest.spyOn(comp, 'load');

      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next('deleted');

      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).toHaveBeenCalled();
    }));

    it('on dismiss should call load', inject([], () => {
      vitest.spyOn(comp, 'load');

      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next();

      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).not.toHaveBeenCalled();
    }));
  });
});
