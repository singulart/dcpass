import dayjs from 'dayjs/esm';

import { IPassContract, NewPassContract } from './pass-contract.model';

export const sampleWithRequiredData: IPassContract = {
  id: 23614,
};

export const sampleWithPartialData: IPassContract = {
  id: 32655,
  agencyAcronym: 'pocket-watch zowie boiling',
  rowId: 32203,
  agency: 'despite',
  awardDate: dayjs('2025-03-03'),
  contractAmount: 32588.29,
  contractNumber: 'glum when loosely',
  startDate: dayjs('2025-03-03'),
  contractingOfficer: 'mozzarella scrabble',
  currentOptionPeriod: 18238,
  description: 'relative boohoo',
  contractTypeDescription: 'inasmuch',
  vendorCity: 'elegant',
  vendorState: 'chip tough remark',
  publishedVersionId: 'pleasant',
  contractingSplst: 'profuse thoughtfully',
  contractDetailsLink: 'substantial',
  contractAdministratorPhone: 'like windy rapid',
  corporatePhone: 'oh',
  corporateEmailAddress: 'yippee',
  recCreatedDate: dayjs('2025-03-03T22:58'),
  recUpdatedDate: dayjs('2025-03-03T21:41'),
  objectId: 3624,
};

export const sampleWithFullData: IPassContract = {
  id: 4506,
  procurementMethodDescription: 'likewise than',
  agencyAcronym: 'concrete ack questionably',
  agencyName: 'ouch lest',
  rowId: 753,
  agency: 'profuse nor joshingly',
  awardDate: dayjs('2025-03-03'),
  contractAmount: 4018.21,
  endDate: dayjs('2025-03-03'),
  contractNumber: 'milestone toady ha',
  startDate: dayjs('2025-03-03'),
  contractStatus: 'metal',
  title: 'once',
  contractingOfficer: 'across easily why',
  fiscalYear: 29949,
  marketType: 'far-off brr',
  commodityCode: 'terrible brightly',
  commodityDescription: 'onto duh hoot',
  currentOptionPeriod: 19876,
  totalOptionPeriods: 16096,
  supplier: 'against',
  description: 'log',
  contractTypeDescription: 'off',
  contractingOfficerEmail: 'intensely',
  vendorAddress: 'curiously however asset',
  vendorCity: 'fooey yawn once',
  vendorState: 'operating',
  vendorZip: 'overdue',
  publishedVersionId: 'rekindle hm hm',
  documentVersion: 'or hence yet',
  lastModified: dayjs('2025-03-04T10:33'),
  contractingSplst: 'unto competent',
  contractingSplstEmail: 'alive phooey',
  source: 'transcend amidst brr',
  contractDetailsLink: 'misjudge vet brightly',
  contractAdministratorName: 'likewise meanwhile',
  contractAdministratorEmail: 'wound',
  contractAdministratorPhone: 'ultimately silt lest',
  contractOfficerPhone: 'er misread',
  cwInternalId: 'rebuff quaintly',
  corporatePhone: 'possible along pace',
  corporateEmailAddress: 'whoa after',
  recCreatedDate: dayjs('2025-03-03T20:04'),
  recUpdatedDate: dayjs('2025-03-03T22:26'),
  dcsLastModDttm: dayjs('2025-03-04T06:36'),
  objectId: 4977,
};

export const sampleWithNewData: NewPassContract = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
