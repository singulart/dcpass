import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPurchaseOrder, NewPurchaseOrder } from '../purchase-order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchaseOrder for edit and NewPurchaseOrderFormGroupInput for create.
 */
type PurchaseOrderFormGroupInput = IPurchaseOrder | PartialWithRequiredKeyOf<NewPurchaseOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPurchaseOrder | NewPurchaseOrder> = Omit<
  T,
  'orderedDate' | 'createDate' | 'dcsLastModDttm' | 'dcsRecCrtDttm'
> & {
  orderedDate?: string | null;
  createDate?: string | null;
  dcsLastModDttm?: string | null;
  dcsRecCrtDttm?: string | null;
};

type PurchaseOrderFormRawValue = FormValueOf<IPurchaseOrder>;

type NewPurchaseOrderFormRawValue = FormValueOf<NewPurchaseOrder>;

type PurchaseOrderFormDefaults = Pick<NewPurchaseOrder, 'id' | 'orderedDate' | 'createDate' | 'dcsLastModDttm' | 'dcsRecCrtDttm'>;

type PurchaseOrderFormGroupContent = {
  id: FormControl<PurchaseOrderFormRawValue['id'] | NewPurchaseOrder['id']>;
  poNumber: FormControl<PurchaseOrderFormRawValue['poNumber']>;
  agencyCode: FormControl<PurchaseOrderFormRawValue['agencyCode']>;
  status: FormControl<PurchaseOrderFormRawValue['status']>;
  requester: FormControl<PurchaseOrderFormRawValue['requester']>;
  requisitionNumber: FormControl<PurchaseOrderFormRawValue['requisitionNumber']>;
  commodityCode: FormControl<PurchaseOrderFormRawValue['commodityCode']>;
  commodityName: FormControl<PurchaseOrderFormRawValue['commodityName']>;
  contractNumber: FormControl<PurchaseOrderFormRawValue['contractNumber']>;
  supplier: FormControl<PurchaseOrderFormRawValue['supplier']>;
  orderedDate: FormControl<PurchaseOrderFormRawValue['orderedDate']>;
  createDate: FormControl<PurchaseOrderFormRawValue['createDate']>;
  poTotal: FormControl<PurchaseOrderFormRawValue['poTotal']>;
  fiscalYear: FormControl<PurchaseOrderFormRawValue['fiscalYear']>;
  poTitle: FormControl<PurchaseOrderFormRawValue['poTitle']>;
  agencyAcronym: FormControl<PurchaseOrderFormRawValue['agencyAcronym']>;
  agencyName: FormControl<PurchaseOrderFormRawValue['agencyName']>;
  dcsLastModDttm: FormControl<PurchaseOrderFormRawValue['dcsLastModDttm']>;
  dcsRecCrtDttm: FormControl<PurchaseOrderFormRawValue['dcsRecCrtDttm']>;
  objectId: FormControl<PurchaseOrderFormRawValue['objectId']>;
};

export type PurchaseOrderFormGroup = FormGroup<PurchaseOrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchaseOrderFormService {
  createPurchaseOrderFormGroup(purchaseOrder?: PurchaseOrderFormGroupInput): PurchaseOrderFormGroup {
    const purchaseOrderRawValue = this.convertPurchaseOrderToPurchaseOrderRawValue({
      ...this.getFormDefaults(),
      ...(purchaseOrder ?? { id: null }),
    });
    return new FormGroup<PurchaseOrderFormGroupContent>({
      id: new FormControl(
        { value: purchaseOrderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      poNumber: new FormControl(purchaseOrderRawValue.poNumber),
      agencyCode: new FormControl(purchaseOrderRawValue.agencyCode),
      status: new FormControl(purchaseOrderRawValue.status),
      requester: new FormControl(purchaseOrderRawValue.requester),
      requisitionNumber: new FormControl(purchaseOrderRawValue.requisitionNumber),
      commodityCode: new FormControl(purchaseOrderRawValue.commodityCode),
      commodityName: new FormControl(purchaseOrderRawValue.commodityName),
      contractNumber: new FormControl(purchaseOrderRawValue.contractNumber),
      supplier: new FormControl(purchaseOrderRawValue.supplier),
      orderedDate: new FormControl(purchaseOrderRawValue.orderedDate),
      createDate: new FormControl(purchaseOrderRawValue.createDate),
      poTotal: new FormControl(purchaseOrderRawValue.poTotal),
      fiscalYear: new FormControl(purchaseOrderRawValue.fiscalYear),
      poTitle: new FormControl(purchaseOrderRawValue.poTitle),
      agencyAcronym: new FormControl(purchaseOrderRawValue.agencyAcronym),
      agencyName: new FormControl(purchaseOrderRawValue.agencyName),
      dcsLastModDttm: new FormControl(purchaseOrderRawValue.dcsLastModDttm),
      dcsRecCrtDttm: new FormControl(purchaseOrderRawValue.dcsRecCrtDttm),
      objectId: new FormControl(purchaseOrderRawValue.objectId),
    });
  }

  getPurchaseOrder(form: PurchaseOrderFormGroup): IPurchaseOrder | NewPurchaseOrder {
    return this.convertPurchaseOrderRawValueToPurchaseOrder(form.getRawValue() as PurchaseOrderFormRawValue | NewPurchaseOrderFormRawValue);
  }

  resetForm(form: PurchaseOrderFormGroup, purchaseOrder: PurchaseOrderFormGroupInput): void {
    const purchaseOrderRawValue = this.convertPurchaseOrderToPurchaseOrderRawValue({ ...this.getFormDefaults(), ...purchaseOrder });
    form.reset({
      ...purchaseOrderRawValue,
      id: { value: purchaseOrderRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PurchaseOrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      orderedDate: currentTime,
      createDate: currentTime,
      dcsLastModDttm: currentTime,
      dcsRecCrtDttm: currentTime,
    };
  }

  private convertPurchaseOrderRawValueToPurchaseOrder(
    rawPurchaseOrder: PurchaseOrderFormRawValue | NewPurchaseOrderFormRawValue,
  ): IPurchaseOrder | NewPurchaseOrder {
    return {
      ...rawPurchaseOrder,
      orderedDate: dayjs(rawPurchaseOrder.orderedDate, DATE_TIME_FORMAT),
      createDate: dayjs(rawPurchaseOrder.createDate, DATE_TIME_FORMAT),
      dcsLastModDttm: dayjs(rawPurchaseOrder.dcsLastModDttm, DATE_TIME_FORMAT),
      dcsRecCrtDttm: dayjs(rawPurchaseOrder.dcsRecCrtDttm, DATE_TIME_FORMAT),
    };
  }

  private convertPurchaseOrderToPurchaseOrderRawValue(
    purchaseOrder: IPurchaseOrder | (Partial<NewPurchaseOrder> & PurchaseOrderFormDefaults),
  ): PurchaseOrderFormRawValue | PartialWithRequiredKeyOf<NewPurchaseOrderFormRawValue> {
    return {
      ...purchaseOrder,
      orderedDate: purchaseOrder.orderedDate ? purchaseOrder.orderedDate.format(DATE_TIME_FORMAT) : undefined,
      createDate: purchaseOrder.createDate ? purchaseOrder.createDate.format(DATE_TIME_FORMAT) : undefined,
      dcsLastModDttm: purchaseOrder.dcsLastModDttm ? purchaseOrder.dcsLastModDttm.format(DATE_TIME_FORMAT) : undefined,
      dcsRecCrtDttm: purchaseOrder.dcsRecCrtDttm ? purchaseOrder.dcsRecCrtDttm.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
