import { defineLink, defineObject } from "@osdk/maker";
import type { LinkType, ObjectTypeDefinition } from "@osdk/maker";

/**
 * DC PASS Ontology-as-code.
 *
 * Grain notes (do not map 1:1 to the raw CSVs):
 * - ContractAward is one extract row, not CWINTERNALID. The CSV repeats the same
 *   CW id per commodity line (~5k ids, up to 93 rows). PK is a surrogate
 *   passContractId (DB id or row hash). CONTRACTNUMBER is a property.
 * - PurchaseOrder is one extract row, not PONUMBER / ponumber_base. Those repeat
 *   when the same PO is copied across agency acronyms (~5k numbers). PK is a
 *   surrogate purchaseOrderId. poNumberBase is still the join key to payments.
 * - Payment is a financial disbursement; many rows have no PASS PO.
 *
 * Backing datasets are left empty for SuperRepo preview. Bind Foundry datasets
 * after ingest; do not point these types at the raw PASS CSVs.
 */

const optional = {
  noNulls: false,
  noEmptyCollections: false,
};

const optionalString = {
  type: "string" as const,
  nullability: optional,
};

const optionalDecimal = {
  type: "decimal" as const,
  nullability: optional,
};

const optionalInteger = {
  type: "integer" as const,
  nullability: optional,
};

const optionalDate = {
  type: "date" as const,
  nullability: optional,
};

const optionalTimestamp = {
  type: "timestamp" as const,
  nullability: optional,
};

const longText = {
  type: "string" as const,
  isLongText: true,
  nullability: optional,
};

// =============================================================================
// Object types
// =============================================================================

export const Agency: ObjectTypeDefinition = defineObject({
  apiName: "agency",
  displayName: "Agency",
  pluralDisplayName: "Agencies",
  description:
    "A District agency identified by AGENCYCODE (TO0, HC0, GA0)",
  titlePropertyApiName: "agencyName",
  primaryKeyPropertyApiName: "agencyCode",
  icon: { locator: "office", color: "#2D72D2" },
  properties: {
    agencyCode: {
      type: "string",
      displayName: "Agency code",
      description: "Agency code (TO0, HC0, GA0).",
    },
    agencyAcronym: {
      ...optionalString,
      displayName: "Acronym",
      description: "Agency acronym (OCTO, DOH)",
    },
    agencyName: {
      type: "string",
      displayName: "Name",
      description: "Full agency name.",
    },
  },
  editsEnabled: true,
  includeEmptyBackingDatasource: true,
});

export const Vendor: ObjectTypeDefinition = defineObject({
  apiName: "vendor",
  displayName: "Vendor",
  pluralDisplayName: "Vendors",
  description:
    "A supplier. PASS has no DUNS/CAGE; vendorId is assigned by the identity pipeline from a normalized legal name.",
  titlePropertyApiName: "legalName",
  primaryKeyPropertyApiName: "vendorId",
  icon: { locator: "shop", color: "#238551" },
  properties: {
    vendorId: {
      type: "string",
      displayName: "Vendor ID",
      description: "Pipeline-generated identifier (not a PASS native key).",
    },
    legalName: {
      type: "string",
      displayName: "Legal name",
      description: "Display name from SUPPLIER / SUPPLIERNAME.",
    },
    address: {
      ...optionalString,
      displayName: "Address",
    },
    city: {
      ...optionalString,
      displayName: "City",
    },
    state: {
      ...optionalString,
      displayName: "State",
    },
    zip: {
      ...optionalString,
      displayName: "ZIP",
    },
    corporatePhone: {
      ...optionalString,
      displayName: "Corporate phone",
    },
    corporateEmail: {
      ...optionalString,
      displayName: "Corporate email",
    },
  },
  editsEnabled: true,
  includeEmptyBackingDatasource: true,
});

export const ContractAward: ObjectTypeDefinition = defineObject({
  apiName: "contractAward",
  displayName: "Contract award",
  pluralDisplayName: "Contract awards",
  description:
    "PASS contract",
  titlePropertyApiName: "title",
  primaryKeyPropertyApiName: "passContractId",
  icon: { locator: "endorsed", color: "#8F4B95" },
  properties: {
    passContractId: {
      type: "string",
      displayName: "PASS contract ID",
      description:
        "Surrogate primary key. PASS_Contracts has no unique native id; use the database id or a row hash at ingest.",
    },
    cwInternalId: {
      ...optionalString,
      displayName: "CW internal ID",
      description:
        "Contract Wizard id. Repeated across commodity lines",
    },
    title: {
      type: "string",
      displayName: "Title",
    },
    description: {
      ...longText,
      displayName: "Description",
    },
    contractStatus: {
      ...optionalString,
      displayName: "Status",
      description: "Published, Closed, or Expired (latest period).",
    },
    marketType: {
      ...optionalString,
      displayName: "Market type",
    },
    procurementMethod: {
      ...optionalString,
      displayName: "Procurement method",
    },
    contractType: {
      ...optionalString,
      displayName: "Contract type",
    },
    fiscalYear: {
      ...optionalInteger,
      displayName: "Fiscal year",
    },
    contractAmount: {
      ...optionalDecimal,
      displayName: "Contract amount",
    },
    awardDate: {
      ...optionalDate,
      displayName: "Award date",
    },
    startDate: {
      ...optionalDate,
      displayName: "Start date",
    },
    endDate: {
      ...optionalDate,
      displayName: "End date",
    },
    currentOptionPeriod: {
      ...optionalString,
      displayName: "Current option period",
    },
    totalOptionPeriods: {
      ...optionalInteger,
      displayName: "Total option periods",
    },
    detailsLink: {
      ...optionalString,
      displayName: "Contract details URL",
    },
    commodityCode: {
      ...optionalString,
      displayName: "Commodity code",
    },
    commodityName: {
      ...optionalString,
      displayName: "Commodity description",
    },
    agencyCode: {
      ...optionalString,
      displayName: "Agency code",
      description: "Foreign key to Agency.",
    },
    vendorId: {
      ...optionalString,
      displayName: "Vendor ID",
      description: "Foreign key to Vendor.",
    },
    contractNumber: {
      ...optionalString,
      displayName: "Contract number",
    },
  },
  editsEnabled: true,
  includeEmptyBackingDatasource: true,
});

export const PurchaseOrder: ObjectTypeDefinition = defineObject({
  apiName: "purchaseOrder",
  displayName: "Purchase order",
  pluralDisplayName: "Purchase orders",
  description:
    "A PASS purchase-order extract row. PONUMBER / ponumber_base are not unique (same PO copied across agency acronyms).",
  titlePropertyApiName: "poTitle",
  primaryKeyPropertyApiName: "purchaseOrderId",
  icon: { locator: "box", color: "#D9822B" },
  properties: {
    purchaseOrderId: {
      type: "string",
      displayName: "Purchase order ID",
      description:
        "Surrogate primary key. Use the database id or a row hash at ingest. Not a PASS column.",
    },
    poNumberBase: {
      ...optionalString,
      displayName: "Normalized PO number",
      description:
        "PONUMBER with -V[n] stripped. Join key to payments; not unique in the extract.",
    },
    poNumber: {
      ...optionalString,
      displayName: "PO number",
      description: "Versioned number as stored (PO590924-V2). Not unique.",
    },
    status: {
      ...optionalString,
      displayName: "Status",
    },
    poTitle: {
      type: "string",
      displayName: "Title",
    },
    poTotal: {
      ...optionalDecimal,
      displayName: "PO total",
    },
    fiscalYear: {
      ...optionalInteger,
      displayName: "Fiscal year",
    },
    orderedDate: {
      ...optionalTimestamp,
      displayName: "Ordered date",
    },
    createDate: {
      ...optionalTimestamp,
      displayName: "Create date",
    },
    requester: {
      ...optionalString,
      displayName: "Requester",
    },
    requisitionNumber: {
      ...optionalString,
      displayName: "Requisition number",
    },
    commodityCode: {
      ...optionalString,
      displayName: "Commodity code",
    },
    commodityName: {
      ...optionalString,
      displayName: "Commodity name",
    },
    contractNumber: {
      ...optionalString,
      displayName: "Contract number",
      description: "Unparsed purchase_order.contractnumber, including messy values.",
    },
    agencyCode: {
      ...optionalString,
      displayName: "Agency code",
      description: "Foreign key to Agency (TO0, AA0).",
    },
    agencyAcronym: {
      ...optionalString,
      displayName: "Agency acronym",
      description:
        "Why PONUMBER repeats: one extract row per acronym (MOAA, MOTA) under the same agency code.",
    },
    vendorId: {
      ...optionalString,
      displayName: "Vendor ID",
      description: "Foreign key to Vendor.",
    },
  },
  editsEnabled: true,
  includeEmptyBackingDatasource: true,
});

export const Payment: ObjectTypeDefinition = defineObject({
  apiName: "payment",
  displayName: "Payment",
  pluralDisplayName: "Payments",
  description:
    "A financial disbursement. About 55% join a PASS PO; the rest are other spend (Medicaid, charter per-pupil, and similar).",
  titlePropertyApiName: "paymentNumber",
  primaryKeyPropertyApiName: "paymentId",
  icon: { locator: "dollar", color: "#0D8050" },
  properties: {
    paymentId: {
      type: "string",
      displayName: "Payment ID",
      description: "Synthetic key (voucher, payment number, date, and amount are not unique).",
    },
    paymentNumber: {
      ...optionalString,
      displayName: "Payment number",
    },
    voucherNumber: {
      ...optionalString,
      displayName: "Voucher number",
    },
    invoiceNumber: {
      ...optionalString,
      displayName: "Invoice number",
    },
    paymentAmount: {
      ...optionalDecimal,
      displayName: "Payment amount",
    },
    voucherAmount: {
      ...optionalDecimal,
      displayName: "Voucher amount",
    },
    paymentDate: {
      ...optionalTimestamp,
      displayName: "Payment date",
    },
    invoiceDate: {
      ...optionalTimestamp,
      displayName: "Invoice date",
    },
    estPaymentDate: {
      ...optionalTimestamp,
      displayName: "Estimated payment date",
    },
    paymentType: {
      ...optionalString,
      displayName: "Payment type",
    },
    transactionCode: {
      ...optionalString,
      displayName: "Transaction code",
    },
    fiscalYear: {
      ...optionalInteger,
      displayName: "Fiscal year",
    },
    agencyCode: {
      ...optionalString,
      displayName: "Agency code",
      description: "Foreign key to Agency.",
    },
    vendorId: {
      ...optionalString,
      displayName: "Vendor ID",
      description: "Foreign key to Vendor.",
    },
    poNumberBase: {
      ...optionalString,
      displayName: "PO number",
      description:
        "Payment PONUMBER, usually already unversioned. Matches purchaseOrder.poNumberBase; that side is not unique.",
    },
    contractNumber: {
      ...optionalString,
      displayName: "Contract number",
      description:
        "Public CONTRACTNUMBER when populated (~25% of payments). Not unique; not a link.",
    },
  },
  editsEnabled: true,
  includeEmptyBackingDatasource: true,
});

// =============================================================================
// Link types
// =============================================================================

export const agencyAwards: LinkType = defineLink({
  apiName: "agencyAwards",
  one: {
    object: Agency,
    metadata: {
      apiName: "awards",
      displayName: "Contract award",
      pluralDisplayName: "Contract awards",
    },
  },
  toMany: {
    object: ContractAward,
    metadata: {
      apiName: "awardingAgency",
      displayName: "Awarding agency",
      pluralDisplayName: "Awarding agencies",
    },
  },
  manyForeignKeyProperty: "agencyCode",
  editsEnabled: true,
});

export const agencyPurchaseOrders: LinkType = defineLink({
  apiName: "agencyPurchaseOrders",
  one: {
    object: Agency,
    metadata: {
      apiName: "purchaseOrders",
      displayName: "Purchase order",
      pluralDisplayName: "Purchase orders",
    },
  },
  toMany: {
    object: PurchaseOrder,
    metadata: {
      apiName: "issuingAgency",
      displayName: "Issuing agency",
      pluralDisplayName: "Issuing agencies",
    },
  },
  manyForeignKeyProperty: "agencyCode",
  editsEnabled: true,
});

export const agencyPayments: LinkType = defineLink({
  apiName: "agencyPayments",
  one: {
    object: Agency,
    metadata: {
      apiName: "payments",
      displayName: "Payment",
      pluralDisplayName: "Payments",
    },
  },
  toMany: {
    object: Payment,
    metadata: {
      apiName: "chargedAgency",
      displayName: "Charged agency",
      pluralDisplayName: "Charged agencies",
    },
  },
  manyForeignKeyProperty: "agencyCode",
  editsEnabled: true,
});

export const vendorAwards: LinkType = defineLink({
  apiName: "vendorAwards",
  one: {
    object: Vendor,
    metadata: {
      apiName: "awards",
      displayName: "Contract award",
      pluralDisplayName: "Contract awards",
    },
  },
  toMany: {
    object: ContractAward,
    metadata: {
      apiName: "awardedVendor",
      displayName: "Awarded vendor",
      pluralDisplayName: "Awarded vendors",
    },
  },
  manyForeignKeyProperty: "vendorId",
  editsEnabled: true,
});

export const vendorPurchaseOrders: LinkType = defineLink({
  apiName: "vendorPurchaseOrders",
  one: {
    object: Vendor,
    metadata: {
      apiName: "purchaseOrders",
      displayName: "Purchase order",
      pluralDisplayName: "Purchase orders",
    },
  },
  toMany: {
    object: PurchaseOrder,
    metadata: {
      apiName: "supplier",
      displayName: "Supplier",
      pluralDisplayName: "Suppliers",
    },
  },
  manyForeignKeyProperty: "vendorId",
  editsEnabled: true,
});

export const vendorPayments: LinkType = defineLink({
  apiName: "vendorPayments",
  one: {
    object: Vendor,
    metadata: {
      apiName: "payments",
      displayName: "Payment",
      pluralDisplayName: "Payments",
    },
  },
  toMany: {
    object: Payment,
    metadata: {
      apiName: "payee",
      displayName: "Payee",
      pluralDisplayName: "Payees",
    },
  },
  manyForeignKeyProperty: "vendorId",
  editsEnabled: true,
});

/** Messy PO→contract attribution (po_contract_map). Join table bound after ingest. */
export const awardPurchaseOrders: LinkType = defineLink({
  apiName: "awardPurchaseOrders",
  many: {
    object: ContractAward,
    metadata: {
      apiName: "purchaseOrders",
      displayName: "Purchase order",
      pluralDisplayName: "Purchase orders",
    },
  },
  toMany: {
    object: PurchaseOrder,
    metadata: {
      apiName: "contractAwards",
      displayName: "Contract award",
      pluralDisplayName: "Contract awards",
    },
  },
  editsEnabled: true,
  includeEmptyBackingDatasource: true,
});

export const purchaseOrderPayments: LinkType = defineLink({
  apiName: "purchaseOrderPayments",
  many: {
    object: PurchaseOrder,
    metadata: {
      apiName: "payments",
      displayName: "Payment",
      pluralDisplayName: "Payments",
    },
  },
  toMany: {
    object: Payment,
    metadata: {
      apiName: "purchaseOrders",
      displayName: "Purchase order",
      pluralDisplayName: "Purchase orders",
    },
  },
  editsEnabled: true,
  includeEmptyBackingDatasource: true,
});
