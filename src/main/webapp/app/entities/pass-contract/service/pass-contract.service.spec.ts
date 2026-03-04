import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPassContract } from '../pass-contract.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../pass-contract.test-samples';

import { PassContractService, RestPassContract } from './pass-contract.service';

const requireRestSample: RestPassContract = {
  ...sampleWithRequiredData,
  awardDate: sampleWithRequiredData.awardDate?.format(DATE_FORMAT),
  endDate: sampleWithRequiredData.endDate?.format(DATE_FORMAT),
  startDate: sampleWithRequiredData.startDate?.format(DATE_FORMAT),
  lastModified: sampleWithRequiredData.lastModified?.toJSON(),
  recCreatedDate: sampleWithRequiredData.recCreatedDate?.toJSON(),
  recUpdatedDate: sampleWithRequiredData.recUpdatedDate?.toJSON(),
  dcsLastModDttm: sampleWithRequiredData.dcsLastModDttm?.toJSON(),
};

describe('PassContract Service', () => {
  let service: PassContractService;
  let httpMock: HttpTestingController;
  let expectedResult: IPassContract | IPassContract[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PassContractService);
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

    it('should create a PassContract', () => {
      const passContract = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(passContract).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PassContract', () => {
      const passContract = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(passContract).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PassContract', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PassContract', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PassContract', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPassContractToCollectionIfMissing', () => {
      it('should add a PassContract to an empty array', () => {
        const passContract: IPassContract = sampleWithRequiredData;
        expectedResult = service.addPassContractToCollectionIfMissing([], passContract);
        expect(expectedResult).toEqual([passContract]);
      });

      it('should not add a PassContract to an array that contains it', () => {
        const passContract: IPassContract = sampleWithRequiredData;
        const passContractCollection: IPassContract[] = [
          {
            ...passContract,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPassContractToCollectionIfMissing(passContractCollection, passContract);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PassContract to an array that doesn't contain it", () => {
        const passContract: IPassContract = sampleWithRequiredData;
        const passContractCollection: IPassContract[] = [sampleWithPartialData];
        expectedResult = service.addPassContractToCollectionIfMissing(passContractCollection, passContract);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(passContract);
      });

      it('should add only unique PassContract to an array', () => {
        const passContractArray: IPassContract[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const passContractCollection: IPassContract[] = [sampleWithRequiredData];
        expectedResult = service.addPassContractToCollectionIfMissing(passContractCollection, ...passContractArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const passContract: IPassContract = sampleWithRequiredData;
        const passContract2: IPassContract = sampleWithPartialData;
        expectedResult = service.addPassContractToCollectionIfMissing([], passContract, passContract2);
        expect(expectedResult).toEqual([passContract, passContract2]);
      });

      it('should accept null and undefined values', () => {
        const passContract: IPassContract = sampleWithRequiredData;
        expectedResult = service.addPassContractToCollectionIfMissing([], null, passContract, undefined);
        expect(expectedResult).toEqual([passContract]);
      });

      it('should return initial array if no PassContract is added', () => {
        const passContractCollection: IPassContract[] = [sampleWithRequiredData];
        expectedResult = service.addPassContractToCollectionIfMissing(passContractCollection, undefined, null);
        expect(expectedResult).toEqual(passContractCollection);
      });
    });

    describe('comparePassContract', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePassContract(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 13513 };
        const entity2 = null;

        const compareResult1 = service.comparePassContract(entity1, entity2);
        const compareResult2 = service.comparePassContract(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 13513 };
        const entity2 = { id: 3727 };

        const compareResult1 = service.comparePassContract(entity1, entity2);
        const compareResult2 = service.comparePassContract(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 13513 };
        const entity2 = { id: 13513 };

        const compareResult1 = service.comparePassContract(entity1, entity2);
        const compareResult2 = service.comparePassContract(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
