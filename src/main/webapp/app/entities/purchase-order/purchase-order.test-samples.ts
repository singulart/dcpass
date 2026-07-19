import dayjs from 'dayjs/esm';

import { IPurchaseOrder, NewPurchaseOrder } from './purchase-order.model';

export const sampleWithRequiredData: IPurchaseOrder = {
  id: 23614,
};

export const sampleWithPartialData: IPurchaseOrder = {
  id: 32655,
  poNumber: 'PO-partial',
  agencyCode: 'AG1',
  status: 'Ordered',
  requester: 'Ada',
  orderedDate: dayjs('2025-03-03T22:58'),
  poTotal: 32588.29,
  fiscalYear: 2025,
  supplier: 'Acme',
  objectId: 3624,
};

export const sampleWithFullData: IPurchaseOrder = {
  id: 4506,
  poNumber: 'PO-full-1',
  agencyCode: 'HC0',
  status: 'Received',
  requester: 'April Richardson',
  requisitionNumber: 'RQ-full-1',
  commodityCode: '9484800',
  commodityName: 'Health Care Services',
  contractNumber: 'CW12345',
  supplier: 'OST, INC.',
  orderedDate: dayjs('2025-03-03T22:58'),
  createDate: dayjs('2025-03-03T21:41'),
  poTotal: 4018.21,
  fiscalYear: 2025,
  poTitle: 'Full title',
  agencyAcronym: 'DOH',
  agencyName: 'DC Health',
  dcsLastModDttm: dayjs('2025-03-04T06:36'),
  dcsRecCrtDttm: dayjs('2025-03-03T20:04'),
  objectId: 4977,
};

export const sampleWithNewData: NewPurchaseOrder = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
