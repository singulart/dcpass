import dayjs from 'dayjs/esm';

export interface IPassPayment {
  id: number;
  agencyCode?: string | null;
  agencyAcronym?: string | null;
  agencyName?: string | null;
  contractNumber?: string | null;
  supplierName?: string | null;
  invoiceNumber?: string | null;
  poNumber?: string | null;
  voucherNumber?: string | null;
  paymentDate?: dayjs.Dayjs | null;
  paymentAmount?: number | null;
  fiscalYear?: number | null;
  transactionCode?: string | null;
  paymentType?: string | null;
  invoiceDate?: dayjs.Dayjs | null;
  estPaymentDate?: dayjs.Dayjs | null;
  paymentNumber?: string | null;
  recordUpdatedDate?: dayjs.Dayjs | null;
  recordCreated?: dayjs.Dayjs | null;
  dcsRecCrtDttm?: dayjs.Dayjs | null;
  dcsLastModDttm?: dayjs.Dayjs | null;
  objectId?: number | null;
}

export type NewPassPayment = Omit<IPassPayment, 'id'> & { id: null };
