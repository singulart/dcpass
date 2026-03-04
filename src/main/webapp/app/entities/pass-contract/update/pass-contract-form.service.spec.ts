import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../pass-contract.test-samples';

import { PassContractFormService } from './pass-contract-form.service';

describe('PassContract Form Service', () => {
  let service: PassContractFormService;

  beforeEach(() => {
    service = TestBed.inject(PassContractFormService);
  });

  describe('Service methods', () => {
    describe('createPassContractFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPassContractFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            procurementMethodDescription: expect.any(Object),
            agencyAcronym: expect.any(Object),
            agencyName: expect.any(Object),
            rowId: expect.any(Object),
            agency: expect.any(Object),
            awardDate: expect.any(Object),
            contractAmount: expect.any(Object),
            endDate: expect.any(Object),
            contractNumber: expect.any(Object),
            startDate: expect.any(Object),
            contractStatus: expect.any(Object),
            title: expect.any(Object),
            contractingOfficer: expect.any(Object),
            fiscalYear: expect.any(Object),
            marketType: expect.any(Object),
            commodityCode: expect.any(Object),
            commodityDescription: expect.any(Object),
            currentOptionPeriod: expect.any(Object),
            totalOptionPeriods: expect.any(Object),
            supplier: expect.any(Object),
            description: expect.any(Object),
            contractTypeDescription: expect.any(Object),
            contractingOfficerEmail: expect.any(Object),
            vendorAddress: expect.any(Object),
            vendorCity: expect.any(Object),
            vendorState: expect.any(Object),
            vendorZip: expect.any(Object),
            publishedVersionId: expect.any(Object),
            documentVersion: expect.any(Object),
            lastModified: expect.any(Object),
            contractingSplst: expect.any(Object),
            contractingSplstEmail: expect.any(Object),
            source: expect.any(Object),
            contractDetailsLink: expect.any(Object),
            contractAdministratorName: expect.any(Object),
            contractAdministratorEmail: expect.any(Object),
            contractAdministratorPhone: expect.any(Object),
            contractOfficerPhone: expect.any(Object),
            cwInternalId: expect.any(Object),
            corporatePhone: expect.any(Object),
            corporateEmailAddress: expect.any(Object),
            recCreatedDate: expect.any(Object),
            recUpdatedDate: expect.any(Object),
            dcsLastModDttm: expect.any(Object),
            objectId: expect.any(Object),
          }),
        );
      });

      it('passing IPassContract should create a new form with FormGroup', () => {
        const formGroup = service.createPassContractFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            procurementMethodDescription: expect.any(Object),
            agencyAcronym: expect.any(Object),
            agencyName: expect.any(Object),
            rowId: expect.any(Object),
            agency: expect.any(Object),
            awardDate: expect.any(Object),
            contractAmount: expect.any(Object),
            endDate: expect.any(Object),
            contractNumber: expect.any(Object),
            startDate: expect.any(Object),
            contractStatus: expect.any(Object),
            title: expect.any(Object),
            contractingOfficer: expect.any(Object),
            fiscalYear: expect.any(Object),
            marketType: expect.any(Object),
            commodityCode: expect.any(Object),
            commodityDescription: expect.any(Object),
            currentOptionPeriod: expect.any(Object),
            totalOptionPeriods: expect.any(Object),
            supplier: expect.any(Object),
            description: expect.any(Object),
            contractTypeDescription: expect.any(Object),
            contractingOfficerEmail: expect.any(Object),
            vendorAddress: expect.any(Object),
            vendorCity: expect.any(Object),
            vendorState: expect.any(Object),
            vendorZip: expect.any(Object),
            publishedVersionId: expect.any(Object),
            documentVersion: expect.any(Object),
            lastModified: expect.any(Object),
            contractingSplst: expect.any(Object),
            contractingSplstEmail: expect.any(Object),
            source: expect.any(Object),
            contractDetailsLink: expect.any(Object),
            contractAdministratorName: expect.any(Object),
            contractAdministratorEmail: expect.any(Object),
            contractAdministratorPhone: expect.any(Object),
            contractOfficerPhone: expect.any(Object),
            cwInternalId: expect.any(Object),
            corporatePhone: expect.any(Object),
            corporateEmailAddress: expect.any(Object),
            recCreatedDate: expect.any(Object),
            recUpdatedDate: expect.any(Object),
            dcsLastModDttm: expect.any(Object),
            objectId: expect.any(Object),
          }),
        );
      });
    });

    describe('getPassContract', () => {
      it('should return NewPassContract for default PassContract initial value', () => {
        const formGroup = service.createPassContractFormGroup(sampleWithNewData);

        const passContract = service.getPassContract(formGroup);

        expect(passContract).toMatchObject(sampleWithNewData);
      });

      it('should return NewPassContract for empty PassContract initial value', () => {
        const formGroup = service.createPassContractFormGroup();

        const passContract = service.getPassContract(formGroup);

        expect(passContract).toMatchObject({});
      });

      it('should return IPassContract', () => {
        const formGroup = service.createPassContractFormGroup(sampleWithRequiredData);

        const passContract = service.getPassContract(formGroup);

        expect(passContract).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPassContract should not enable id FormControl', () => {
        const formGroup = service.createPassContractFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPassContract should disable id FormControl', () => {
        const formGroup = service.createPassContractFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
