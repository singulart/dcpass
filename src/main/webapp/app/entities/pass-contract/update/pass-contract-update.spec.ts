import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IPassContract } from '../pass-contract.model';
import { PassContractService } from '../service/pass-contract.service';

import { PassContractFormService } from './pass-contract-form.service';
import { PassContractUpdate } from './pass-contract-update';

describe('PassContract Management Update Component', () => {
  let comp: PassContractUpdate;
  let fixture: ComponentFixture<PassContractUpdate>;
  let activatedRoute: ActivatedRoute;
  let passContractFormService: PassContractFormService;
  let passContractService: PassContractService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(PassContractUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    passContractFormService = TestBed.inject(PassContractFormService);
    passContractService = TestBed.inject(PassContractService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const passContract: IPassContract = { id: 3727 };

      activatedRoute.data = of({ passContract });
      comp.ngOnInit();

      expect(comp.passContract).toEqual(passContract);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPassContract>>();
      const passContract = { id: 13513 };
      vitest.spyOn(passContractFormService, 'getPassContract').mockReturnValue(passContract);
      vitest.spyOn(passContractService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ passContract });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(new HttpResponse({ body: passContract }));
      saveSubject.complete();

      // THEN
      expect(passContractFormService.getPassContract).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(passContractService.update).toHaveBeenCalledWith(expect.objectContaining(passContract));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPassContract>>();
      const passContract = { id: 13513 };
      vitest.spyOn(passContractFormService, 'getPassContract').mockReturnValue({ id: null });
      vitest.spyOn(passContractService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ passContract: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(new HttpResponse({ body: passContract }));
      saveSubject.complete();

      // THEN
      expect(passContractFormService.getPassContract).toHaveBeenCalled();
      expect(passContractService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPassContract>>();
      const passContract = { id: 13513 };
      vitest.spyOn(passContractService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ passContract });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(passContractService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
