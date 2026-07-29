export interface IFy2026AgencySpend {
  agency?: string | null;
  agencyAcronym?: string | null;
  spend?: number | null;
}

export interface IFy2026ContractSpend {
  contractTitle?: string | null;
  contractNumber?: string | null;
  spend?: number | null;
  /** Aggregated under-$50k contracts; not drillable. */
  isOthers?: boolean;
}

export interface IFy2026PoSpend {
  purchaseOrderId?: number | null;
  poNumber?: string | null;
  poTitle?: string | null;
  spend?: number | null;
  /** Aggregated under-$50k POs; not clickable. */
  isOthers?: boolean;
}

export type Fy2026ChartLevel = 'agency' | 'contract' | 'po';
