import dayjs from 'dayjs/esm';

import { IPassPayment, NewPassPayment } from './pass-payment.model';

export const sampleWithRequiredData: IPassPayment = {
  id: 23614,
};

export const sampleWithPartialData: IPassPayment = {
  id: 32655,
  paymentNumber: 'PAY-partial',
  agencyCode: 'AG1',
  supplierName: 'Acme',
  paymentDate: dayjs('2025-03-03T22:58'),
  paymentAmount: 32588.29,
  fiscalYear: 2025,
  objectId: 3624,
};

export const sampleWithFullData: IPassPayment = {
  id: 4506,
  agencyCode: 'HC0',
  agencyAcronym: 'DOH',
  agencyName: 'DC Health',
  contractNumber: 'CW12345',
  supplierName: 'OST, INC.',
  invoiceNumber: 'INV-full-1',
  poNumber: 'PO-full-1',
  voucherNumber: 'VB012220',
  paymentDate: dayjs('2025-03-03T22:58'),
  paymentAmount: 4018.21,
  fiscalYear: 2025,
  transactionCode: '242',
  paymentType: 'FIRST CLASS MAIL',
  invoiceDate: dayjs('2025-03-01T10:00'),
  estPaymentDate: dayjs('2025-03-02T10:00'),
  paymentNumber: '006320309',
  recordUpdatedDate: dayjs('2025-03-04T06:36'),
  recordCreated: dayjs('2025-03-03T20:04'),
  dcsRecCrtDttm: dayjs('2025-03-03T20:04'),
  dcsLastModDttm: dayjs('2025-03-04T06:36'),
  objectId: 4977,
};

export const sampleWithNewData: NewPassPayment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
