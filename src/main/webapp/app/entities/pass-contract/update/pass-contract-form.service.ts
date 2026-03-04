import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPassContract, NewPassContract } from '../pass-contract.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPassContract for edit and NewPassContractFormGroupInput for create.
 */
type PassContractFormGroupInput = IPassContract | PartialWithRequiredKeyOf<NewPassContract>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPassContract | NewPassContract> = Omit<
  T,
  'lastModified' | 'recCreatedDate' | 'recUpdatedDate' | 'dcsLastModDttm'
> & {
  lastModified?: string | null;
  recCreatedDate?: string | null;
  recUpdatedDate?: string | null;
  dcsLastModDttm?: string | null;
};

type PassContractFormRawValue = FormValueOf<IPassContract>;

type NewPassContractFormRawValue = FormValueOf<NewPassContract>;

type PassContractFormDefaults = Pick<NewPassContract, 'id' | 'lastModified' | 'recCreatedDate' | 'recUpdatedDate' | 'dcsLastModDttm'>;

type PassContractFormGroupContent = {
  id: FormControl<PassContractFormRawValue['id'] | NewPassContract['id']>;
  procurementMethodDescription: FormControl<PassContractFormRawValue['procurementMethodDescription']>;
  agencyAcronym: FormControl<PassContractFormRawValue['agencyAcronym']>;
  agencyName: FormControl<PassContractFormRawValue['agencyName']>;
  rowId: FormControl<PassContractFormRawValue['rowId']>;
  agency: FormControl<PassContractFormRawValue['agency']>;
  awardDate: FormControl<PassContractFormRawValue['awardDate']>;
  contractAmount: FormControl<PassContractFormRawValue['contractAmount']>;
  endDate: FormControl<PassContractFormRawValue['endDate']>;
  contractNumber: FormControl<PassContractFormRawValue['contractNumber']>;
  startDate: FormControl<PassContractFormRawValue['startDate']>;
  contractStatus: FormControl<PassContractFormRawValue['contractStatus']>;
  title: FormControl<PassContractFormRawValue['title']>;
  contractingOfficer: FormControl<PassContractFormRawValue['contractingOfficer']>;
  fiscalYear: FormControl<PassContractFormRawValue['fiscalYear']>;
  marketType: FormControl<PassContractFormRawValue['marketType']>;
  commodityCode: FormControl<PassContractFormRawValue['commodityCode']>;
  commodityDescription: FormControl<PassContractFormRawValue['commodityDescription']>;
  currentOptionPeriod: FormControl<PassContractFormRawValue['currentOptionPeriod']>;
  totalOptionPeriods: FormControl<PassContractFormRawValue['totalOptionPeriods']>;
  supplier: FormControl<PassContractFormRawValue['supplier']>;
  description: FormControl<PassContractFormRawValue['description']>;
  contractTypeDescription: FormControl<PassContractFormRawValue['contractTypeDescription']>;
  contractingOfficerEmail: FormControl<PassContractFormRawValue['contractingOfficerEmail']>;
  vendorAddress: FormControl<PassContractFormRawValue['vendorAddress']>;
  vendorCity: FormControl<PassContractFormRawValue['vendorCity']>;
  vendorState: FormControl<PassContractFormRawValue['vendorState']>;
  vendorZip: FormControl<PassContractFormRawValue['vendorZip']>;
  publishedVersionId: FormControl<PassContractFormRawValue['publishedVersionId']>;
  documentVersion: FormControl<PassContractFormRawValue['documentVersion']>;
  lastModified: FormControl<PassContractFormRawValue['lastModified']>;
  contractingSplst: FormControl<PassContractFormRawValue['contractingSplst']>;
  contractingSplstEmail: FormControl<PassContractFormRawValue['contractingSplstEmail']>;
  source: FormControl<PassContractFormRawValue['source']>;
  contractDetailsLink: FormControl<PassContractFormRawValue['contractDetailsLink']>;
  contractAdministratorName: FormControl<PassContractFormRawValue['contractAdministratorName']>;
  contractAdministratorEmail: FormControl<PassContractFormRawValue['contractAdministratorEmail']>;
  contractAdministratorPhone: FormControl<PassContractFormRawValue['contractAdministratorPhone']>;
  contractOfficerPhone: FormControl<PassContractFormRawValue['contractOfficerPhone']>;
  cwInternalId: FormControl<PassContractFormRawValue['cwInternalId']>;
  corporatePhone: FormControl<PassContractFormRawValue['corporatePhone']>;
  corporateEmailAddress: FormControl<PassContractFormRawValue['corporateEmailAddress']>;
  recCreatedDate: FormControl<PassContractFormRawValue['recCreatedDate']>;
  recUpdatedDate: FormControl<PassContractFormRawValue['recUpdatedDate']>;
  dcsLastModDttm: FormControl<PassContractFormRawValue['dcsLastModDttm']>;
  objectId: FormControl<PassContractFormRawValue['objectId']>;
};

export type PassContractFormGroup = FormGroup<PassContractFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PassContractFormService {
  createPassContractFormGroup(passContract?: PassContractFormGroupInput): PassContractFormGroup {
    const passContractRawValue = this.convertPassContractToPassContractRawValue({
      ...this.getFormDefaults(),
      ...(passContract ?? { id: null }),
    });
    return new FormGroup<PassContractFormGroupContent>({
      id: new FormControl(
        { value: passContractRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      procurementMethodDescription: new FormControl(passContractRawValue.procurementMethodDescription),
      agencyAcronym: new FormControl(passContractRawValue.agencyAcronym),
      agencyName: new FormControl(passContractRawValue.agencyName),
      rowId: new FormControl(passContractRawValue.rowId),
      agency: new FormControl(passContractRawValue.agency),
      awardDate: new FormControl(passContractRawValue.awardDate),
      contractAmount: new FormControl(passContractRawValue.contractAmount),
      endDate: new FormControl(passContractRawValue.endDate),
      contractNumber: new FormControl(passContractRawValue.contractNumber),
      startDate: new FormControl(passContractRawValue.startDate),
      contractStatus: new FormControl(passContractRawValue.contractStatus),
      title: new FormControl(passContractRawValue.title),
      contractingOfficer: new FormControl(passContractRawValue.contractingOfficer),
      fiscalYear: new FormControl(passContractRawValue.fiscalYear),
      marketType: new FormControl(passContractRawValue.marketType),
      commodityCode: new FormControl(passContractRawValue.commodityCode),
      commodityDescription: new FormControl(passContractRawValue.commodityDescription),
      currentOptionPeriod: new FormControl(passContractRawValue.currentOptionPeriod),
      totalOptionPeriods: new FormControl(passContractRawValue.totalOptionPeriods),
      supplier: new FormControl(passContractRawValue.supplier),
      description: new FormControl(passContractRawValue.description),
      contractTypeDescription: new FormControl(passContractRawValue.contractTypeDescription),
      contractingOfficerEmail: new FormControl(passContractRawValue.contractingOfficerEmail),
      vendorAddress: new FormControl(passContractRawValue.vendorAddress),
      vendorCity: new FormControl(passContractRawValue.vendorCity),
      vendorState: new FormControl(passContractRawValue.vendorState),
      vendorZip: new FormControl(passContractRawValue.vendorZip),
      publishedVersionId: new FormControl(passContractRawValue.publishedVersionId),
      documentVersion: new FormControl(passContractRawValue.documentVersion),
      lastModified: new FormControl(passContractRawValue.lastModified),
      contractingSplst: new FormControl(passContractRawValue.contractingSplst),
      contractingSplstEmail: new FormControl(passContractRawValue.contractingSplstEmail),
      source: new FormControl(passContractRawValue.source),
      contractDetailsLink: new FormControl(passContractRawValue.contractDetailsLink),
      contractAdministratorName: new FormControl(passContractRawValue.contractAdministratorName),
      contractAdministratorEmail: new FormControl(passContractRawValue.contractAdministratorEmail),
      contractAdministratorPhone: new FormControl(passContractRawValue.contractAdministratorPhone),
      contractOfficerPhone: new FormControl(passContractRawValue.contractOfficerPhone),
      cwInternalId: new FormControl(passContractRawValue.cwInternalId),
      corporatePhone: new FormControl(passContractRawValue.corporatePhone),
      corporateEmailAddress: new FormControl(passContractRawValue.corporateEmailAddress),
      recCreatedDate: new FormControl(passContractRawValue.recCreatedDate),
      recUpdatedDate: new FormControl(passContractRawValue.recUpdatedDate),
      dcsLastModDttm: new FormControl(passContractRawValue.dcsLastModDttm),
      objectId: new FormControl(passContractRawValue.objectId),
    });
  }

  getPassContract(form: PassContractFormGroup): IPassContract | NewPassContract {
    return this.convertPassContractRawValueToPassContract(form.getRawValue() as PassContractFormRawValue | NewPassContractFormRawValue);
  }

  resetForm(form: PassContractFormGroup, passContract: PassContractFormGroupInput): void {
    const passContractRawValue = this.convertPassContractToPassContractRawValue({ ...this.getFormDefaults(), ...passContract });
    form.reset({
      ...passContractRawValue,
      id: { value: passContractRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PassContractFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastModified: currentTime,
      recCreatedDate: currentTime,
      recUpdatedDate: currentTime,
      dcsLastModDttm: currentTime,
    };
  }

  private convertPassContractRawValueToPassContract(
    rawPassContract: PassContractFormRawValue | NewPassContractFormRawValue,
  ): IPassContract | NewPassContract {
    return {
      ...rawPassContract,
      lastModified: dayjs(rawPassContract.lastModified, DATE_TIME_FORMAT),
      recCreatedDate: dayjs(rawPassContract.recCreatedDate, DATE_TIME_FORMAT),
      recUpdatedDate: dayjs(rawPassContract.recUpdatedDate, DATE_TIME_FORMAT),
      dcsLastModDttm: dayjs(rawPassContract.dcsLastModDttm, DATE_TIME_FORMAT),
    };
  }

  private convertPassContractToPassContractRawValue(
    passContract: IPassContract | (Partial<NewPassContract> & PassContractFormDefaults),
  ): PassContractFormRawValue | PartialWithRequiredKeyOf<NewPassContractFormRawValue> {
    return {
      ...passContract,
      lastModified: passContract.lastModified ? passContract.lastModified.format(DATE_TIME_FORMAT) : undefined,
      recCreatedDate: passContract.recCreatedDate ? passContract.recCreatedDate.format(DATE_TIME_FORMAT) : undefined,
      recUpdatedDate: passContract.recUpdatedDate ? passContract.recUpdatedDate.format(DATE_TIME_FORMAT) : undefined,
      dcsLastModDttm: passContract.dcsLastModDttm ? passContract.dcsLastModDttm.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
