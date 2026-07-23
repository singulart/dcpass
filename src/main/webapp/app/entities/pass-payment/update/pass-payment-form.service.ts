import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPassPayment, NewPassPayment } from '../pass-payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPassPayment for edit and NewPassPaymentFormGroupInput for create.
 */
type PassPaymentFormGroupInput = IPassPayment | PartialWithRequiredKeyOf<NewPassPayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPassPayment | NewPassPayment> = Omit<
  T,
  'paymentDate' | 'invoiceDate' | 'estPaymentDate' | 'recordUpdatedDate' | 'recordCreated' | 'dcsRecCrtDttm' | 'dcsLastModDttm'
> & {
  paymentDate?: string | null;
  invoiceDate?: string | null;
  estPaymentDate?: string | null;
  recordUpdatedDate?: string | null;
  recordCreated?: string | null;
  dcsRecCrtDttm?: string | null;
  dcsLastModDttm?: string | null;
};

type PassPaymentFormRawValue = FormValueOf<IPassPayment>;

type NewPassPaymentFormRawValue = FormValueOf<NewPassPayment>;

type PassPaymentFormDefaults = Pick<
  NewPassPayment,
  'id' | 'paymentDate' | 'invoiceDate' | 'estPaymentDate' | 'recordUpdatedDate' | 'recordCreated' | 'dcsRecCrtDttm' | 'dcsLastModDttm'
>;

type PassPaymentFormGroupContent = {
  id: FormControl<PassPaymentFormRawValue['id'] | NewPassPayment['id']>;
  agencyCode: FormControl<PassPaymentFormRawValue['agencyCode']>;
  agencyAcronym: FormControl<PassPaymentFormRawValue['agencyAcronym']>;
  agencyName: FormControl<PassPaymentFormRawValue['agencyName']>;
  contractNumber: FormControl<PassPaymentFormRawValue['contractNumber']>;
  supplierName: FormControl<PassPaymentFormRawValue['supplierName']>;
  invoiceNumber: FormControl<PassPaymentFormRawValue['invoiceNumber']>;
  poNumber: FormControl<PassPaymentFormRawValue['poNumber']>;
  voucherNumber: FormControl<PassPaymentFormRawValue['voucherNumber']>;
  paymentDate: FormControl<PassPaymentFormRawValue['paymentDate']>;
  paymentAmount: FormControl<PassPaymentFormRawValue['paymentAmount']>;
  fiscalYear: FormControl<PassPaymentFormRawValue['fiscalYear']>;
  transactionCode: FormControl<PassPaymentFormRawValue['transactionCode']>;
  paymentType: FormControl<PassPaymentFormRawValue['paymentType']>;
  invoiceDate: FormControl<PassPaymentFormRawValue['invoiceDate']>;
  estPaymentDate: FormControl<PassPaymentFormRawValue['estPaymentDate']>;
  paymentNumber: FormControl<PassPaymentFormRawValue['paymentNumber']>;
  recordUpdatedDate: FormControl<PassPaymentFormRawValue['recordUpdatedDate']>;
  recordCreated: FormControl<PassPaymentFormRawValue['recordCreated']>;
  dcsRecCrtDttm: FormControl<PassPaymentFormRawValue['dcsRecCrtDttm']>;
  dcsLastModDttm: FormControl<PassPaymentFormRawValue['dcsLastModDttm']>;
  objectId: FormControl<PassPaymentFormRawValue['objectId']>;
};

export type PassPaymentFormGroup = FormGroup<PassPaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PassPaymentFormService {
  createPassPaymentFormGroup(passPayment?: PassPaymentFormGroupInput): PassPaymentFormGroup {
    const passPaymentRawValue = this.convertPassPaymentToPassPaymentRawValue({
      ...this.getFormDefaults(),
      ...(passPayment ?? { id: null }),
    });
    return new FormGroup<PassPaymentFormGroupContent>({
      id: new FormControl(
        { value: passPaymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      agencyCode: new FormControl(passPaymentRawValue.agencyCode),
      agencyAcronym: new FormControl(passPaymentRawValue.agencyAcronym),
      agencyName: new FormControl(passPaymentRawValue.agencyName),
      contractNumber: new FormControl(passPaymentRawValue.contractNumber),
      supplierName: new FormControl(passPaymentRawValue.supplierName),
      invoiceNumber: new FormControl(passPaymentRawValue.invoiceNumber),
      poNumber: new FormControl(passPaymentRawValue.poNumber),
      voucherNumber: new FormControl(passPaymentRawValue.voucherNumber),
      paymentDate: new FormControl(passPaymentRawValue.paymentDate),
      paymentAmount: new FormControl(passPaymentRawValue.paymentAmount),
      fiscalYear: new FormControl(passPaymentRawValue.fiscalYear),
      transactionCode: new FormControl(passPaymentRawValue.transactionCode),
      paymentType: new FormControl(passPaymentRawValue.paymentType),
      invoiceDate: new FormControl(passPaymentRawValue.invoiceDate),
      estPaymentDate: new FormControl(passPaymentRawValue.estPaymentDate),
      paymentNumber: new FormControl(passPaymentRawValue.paymentNumber),
      recordUpdatedDate: new FormControl(passPaymentRawValue.recordUpdatedDate),
      recordCreated: new FormControl(passPaymentRawValue.recordCreated),
      dcsRecCrtDttm: new FormControl(passPaymentRawValue.dcsRecCrtDttm),
      dcsLastModDttm: new FormControl(passPaymentRawValue.dcsLastModDttm),
      objectId: new FormControl(passPaymentRawValue.objectId),
    });
  }

  getPassPayment(form: PassPaymentFormGroup): IPassPayment | NewPassPayment {
    return this.convertPassPaymentRawValueToPassPayment(form.getRawValue() as PassPaymentFormRawValue | NewPassPaymentFormRawValue);
  }

  resetForm(form: PassPaymentFormGroup, passPayment: PassPaymentFormGroupInput): void {
    const passPaymentRawValue = this.convertPassPaymentToPassPaymentRawValue({ ...this.getFormDefaults(), ...passPayment });
    form.reset({
      ...passPaymentRawValue,
      id: { value: passPaymentRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PassPaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      paymentDate: currentTime,
      invoiceDate: currentTime,
      estPaymentDate: currentTime,
      recordUpdatedDate: currentTime,
      recordCreated: currentTime,
      dcsRecCrtDttm: currentTime,
      dcsLastModDttm: currentTime,
    };
  }

  private convertPassPaymentRawValueToPassPayment(
    rawPassPayment: PassPaymentFormRawValue | NewPassPaymentFormRawValue,
  ): IPassPayment | NewPassPayment {
    return {
      ...rawPassPayment,
      paymentDate: dayjs(rawPassPayment.paymentDate, DATE_TIME_FORMAT),
      invoiceDate: dayjs(rawPassPayment.invoiceDate, DATE_TIME_FORMAT),
      estPaymentDate: dayjs(rawPassPayment.estPaymentDate, DATE_TIME_FORMAT),
      recordUpdatedDate: dayjs(rawPassPayment.recordUpdatedDate, DATE_TIME_FORMAT),
      recordCreated: dayjs(rawPassPayment.recordCreated, DATE_TIME_FORMAT),
      dcsRecCrtDttm: dayjs(rawPassPayment.dcsRecCrtDttm, DATE_TIME_FORMAT),
      dcsLastModDttm: dayjs(rawPassPayment.dcsLastModDttm, DATE_TIME_FORMAT),
    };
  }

  private convertPassPaymentToPassPaymentRawValue(
    passPayment: IPassPayment | (Partial<NewPassPayment> & PassPaymentFormDefaults),
  ): PassPaymentFormRawValue | PartialWithRequiredKeyOf<NewPassPaymentFormRawValue> {
    return {
      ...passPayment,
      paymentDate: passPayment.paymentDate ? passPayment.paymentDate.format(DATE_TIME_FORMAT) : undefined,
      invoiceDate: passPayment.invoiceDate ? passPayment.invoiceDate.format(DATE_TIME_FORMAT) : undefined,
      estPaymentDate: passPayment.estPaymentDate ? passPayment.estPaymentDate.format(DATE_TIME_FORMAT) : undefined,
      recordUpdatedDate: passPayment.recordUpdatedDate ? passPayment.recordUpdatedDate.format(DATE_TIME_FORMAT) : undefined,
      recordCreated: passPayment.recordCreated ? passPayment.recordCreated.format(DATE_TIME_FORMAT) : undefined,
      dcsRecCrtDttm: passPayment.dcsRecCrtDttm ? passPayment.dcsRecCrtDttm.format(DATE_TIME_FORMAT) : undefined,
      dcsLastModDttm: passPayment.dcsLastModDttm ? passPayment.dcsLastModDttm.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
