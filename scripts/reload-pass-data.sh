#!/usr/bin/env bash
# Truncate PASS fact tables (keeps it_commodity_code), reset sequences,
# load contracts / purchase orders / payments from gzip CSVs, refresh FY2026 MVs.
#
# Usage:
#   ./scripts/reload-pass-data.sh
#   ./scripts/reload-pass-data.sh [contracts.gz] [purchase_orders.gz] [payments.gz]
#
# Env (psql defaults apply as well: PGHOST, PGPORT, PGUSER, PGDATABASE, PGPASSWORD):
#   PGHOST=localhost  PGPORT=5432  PGUSER=dcpass  PGDATABASE=dcpass
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

export PGHOST="${PGHOST:-localhost}"
export PGPORT="${PGPORT:-5432}"
export PGUSER="${PGUSER:-dcpass}"
export PGDATABASE="${PGDATABASE:-dcpass}"

CONTRACTS_GZ="${1:-$ROOT/PASS_Contracts.csv.gz}"
POS_GZ="${2:-$ROOT/PASS_Purchase_Orders.csv.gz}"
PAYMENTS_GZ="${3:-$ROOT/PASS_Payments.csv.gz}"

# Prefer the newer PASS export name when the canonical PO path is missing.
if [[ ! -f "$POS_GZ" && -f "$ROOT/Purchase_Orders_from_PASS.csv.gz" ]]; then
  POS_GZ="$ROOT/Purchase_Orders_from_PASS.csv.gz"
fi

for f in "$CONTRACTS_GZ" "$POS_GZ" "$PAYMENTS_GZ"; do
  if [[ ! -f "$f" ]]; then
    echo "ERROR: missing data file: $f" >&2
    exit 1
  fi
done

if ! command -v psql >/dev/null 2>&1; then
  echo "ERROR: psql not found on PATH" >&2
  exit 1
fi

psql_q() {
  psql -v ON_ERROR_STOP=1 -q "$@"
}

# Strip a UTF-8 BOM from the first line (PASS PO/payment exports include one).
# Payments use US-style timestamps; DateStyle MDY lets Postgres accept them.
copy_gz() {
  local gz="$1"
  local table_and_cols="$2"
  echo "Loading ${gz##*/} → ${table_and_cols%% *} ..."
  (
    if [[ "${3:-}" == "mdy" ]]; then
      export PGOPTIONS="-c DateStyle=ISO,MDY"
    fi
    gunzip -c "$gz" \
      | sed $'1s/^\xEF\xBB\xBF//' \
      | psql_q -c "\\copy ${table_and_cols} FROM STDIN WITH (FORMAT csv, HEADER true)"
  )
}

CONTRACT_COLS="PROCUREMENTMETHODDESCRIPTION, AGENCY_ACRONYM, AGENCY_NAME, ROW_ID, AGENCY, AWARDDATE, CONTRACTAMOUNT, ENDDATE, CONTRACTNUMBER, STARTDATE, CONTRACTSTATUS, TITLE, CONTRACTINGOFFICER, FISCALYEAR, MARKETTYPE, COMMODITYCODE, COMMODITYDESCRIPTION, CURRENTOPTIONPERIOD, TOTALOPTIONPERIODS, SUPPLIER, DESCRIPTION, CONTRACTTYPEDESCRIPTION, CONTRACTINGOFFICEREMAIL, VENDORADDRESS, VENDORCITY, VENDORSTATE, VENDORZIP, PUBLISHEDVERSIONID, DOCUMENTVERSION, LASTMODIFIED, CONTRACTINGSPLST, CONTRACTINGSPLSTEMAIL, SOURCE, CONTRACT_DETAILS_LINK, CONTRACTADMINISTRATORNAME, CONTRACTADMINISTRATOREMAIL, CONTRACTADMINISTRATORPHONE, CONTRACTOFFICERPHONE, CWINTERNALID, CORPORATEPHONE, CORPORATEEMAILADDRESS, REC_CREATED_DATE, REC_UPDATED_DATE, DCS_LAST_MOD_DTTM, OBJECTID"

PO_COLS="PONUMBER, AGENCYCODE, STATUS, REQUESTER, REQUISTIONNUMBER, COMMODITYCODE, COMMODITYNAME, CONTRACTNUMBER, SUPPLIER, ORDEREDDATE, CREATEDATE, POTOTAL, FISCALYEAR, POTITLE, AGENCY_ACRONYM, AGENCY_NAME, DCS_LAST_MOD_DTTM, DCS_REC_CRT_DTTM, OBJECT_ID"

# Column order matches the current PASS_Payments.csv.gz header (no OBJECTID).
PAYMENT_COLS="AGENCY_ACRONYM, AGENCY_NAME, AGENCYCODE, CONTRACTNUMBER, SUPPLIERNAME, INVOICENUMBER, PONUMBER, VOUCHERNUMBER, PAYMENTDATE, PAYMENTAMOUNT, FISCALYEAR, PAYMENTTYPE, INVOICEDATE, ESTPAYMENTDATE, PAYMENTNUMBER, TRANSACTION_CODE, VOUCHERAMOUNT, RECORDUPDATEDDATE, RECORDCREATED, DCS_REC_CRT_DTTM, DCS_LAST_MOD_DTTM"

echo "Truncating PASS tables (keeping it_commodity_code) and resetting sequences..."
psql_q <<'SQL'
TRUNCATE TABLE pass_payment, purchase_order, pass_contract;
ALTER SEQUENCE pass_payment_seq RESTART WITH 1;
ALTER SEQUENCE purchase_order_seq RESTART WITH 1;
ALTER SEQUENCE pass_contract_seq RESTART WITH 1;
SQL

copy_gz "$CONTRACTS_GZ" "pass_contract (${CONTRACT_COLS})"
copy_gz "$POS_GZ" "purchase_order (${PO_COLS})"
copy_gz "$PAYMENTS_GZ" "pass_payment (${PAYMENT_COLS})" mdy

echo "Syncing sequences to max(id)..."
psql_q <<'SQL'
SELECT setval('pass_contract_seq', COALESCE((SELECT MAX(id) FROM pass_contract), 1), true);
SELECT setval('purchase_order_seq', COALESCE((SELECT MAX(id) FROM purchase_order), 1), true);
SELECT setval('pass_payment_seq', COALESCE((SELECT MAX(id) FROM pass_payment), 1), true);
SQL

echo "Refreshing FY2026 materialized views..."
psql_q <<'SQL'
REFRESH MATERIALIZED VIEW mv_fy2026_it_spend_by_agency;
REFRESH MATERIALIZED VIEW mv_fy2026_it_awarded_pos_by_agency;
SQL

echo "Done:"
psql_q -c "SELECT 'pass_contract' AS table, COUNT(*) FROM pass_contract
UNION ALL SELECT 'purchase_order', COUNT(*) FROM purchase_order
UNION ALL SELECT 'pass_payment', COUNT(*) FROM pass_payment
UNION ALL SELECT 'it_commodity_code', COUNT(*) FROM it_commodity_code
ORDER BY 1;"
