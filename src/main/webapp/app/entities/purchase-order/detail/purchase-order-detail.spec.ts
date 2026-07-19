import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { PurchaseOrderDetail } from './purchase-order-detail';

describe('PurchaseOrder Management Detail Component', () => {
  let comp: PurchaseOrderDetail;
  let fixture: ComponentFixture<PurchaseOrderDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./purchase-order-detail').then(m => m.PurchaseOrderDetail),
              resolve: { purchaseOrder: () => of({ id: 13513 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchaseOrderDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load purchaseOrder on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PurchaseOrderDetail);

      expect(instance.purchaseOrder()).toEqual(expect.objectContaining({ id: 13513 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(window.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
