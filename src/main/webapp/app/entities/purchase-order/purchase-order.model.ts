import dayjs from 'dayjs/esm';

export interface IPurchaseOrder {
  id: number;
  poNumber?: string | null;
  agencyCode?: string | null;
  status?: string | null;
  requester?: string | null;
  requisitionNumber?: string | null;
  commodityCode?: string | null;
  commodityName?: string | null;
  contractNumber?: string | null;
  supplier?: string | null;
  orderedDate?: dayjs.Dayjs | null;
  createDate?: dayjs.Dayjs | null;
  poTotal?: number | null;
  fiscalYear?: number | null;
  poTitle?: string | null;
  agencyAcronym?: string | null;
  agencyName?: string | null;
  dcsLastModDttm?: dayjs.Dayjs | null;
  dcsRecCrtDttm?: dayjs.Dayjs | null;
  objectId?: number | null;
}

export type NewPurchaseOrder = Omit<IPurchaseOrder, 'id'> & { id: null };
