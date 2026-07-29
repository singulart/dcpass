export interface IFy2026AgencySpend {
  agency?: string | null;
  agencyAcronym?: string | null;
  spend?: number | null;
}

export interface IFy2026ContractSpend {
  contractTitle?: string | null;
  contractNumber?: string | null;
  spend?: number | null;
}

export interface IFy2026PoSpend {
  poNumber?: string | null;
  poTitle?: string | null;
  spend?: number | null;
}

export type Fy2026ChartLevel = 'agency' | 'contract' | 'po';
