import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';

import { PurchaseOrderService } from '../service/purchase-order.service';

import { PurchaseOrderDeleteDialog } from './purchase-order-delete-dialog';

describe('PurchaseOrder Management Delete Component', () => {
  let comp: PurchaseOrderDeleteDialog;
  let fixture: ComponentFixture<PurchaseOrderDeleteDialog>;
  let service: PurchaseOrderService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    });
    fixture = TestBed.createComponent(PurchaseOrderDeleteDialog);
    comp = fixture.componentInstance;
    service = TestBed.inject(PurchaseOrderService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('should call delete service on confirmDelete', () => {
      vitest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));
      vitest.spyOn(mockActiveModal, 'close');

      comp.confirmDelete(123);

      expect(service.delete).toHaveBeenCalledWith(123);
      expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
    });

    it('should not call delete service on clear', () => {
      vitest.spyOn(service, 'delete');
      vitest.spyOn(mockActiveModal, 'close');
      vitest.spyOn(mockActiveModal, 'dismiss');

      comp.cancel();

      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
