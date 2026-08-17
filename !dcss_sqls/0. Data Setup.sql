-- The definition of DCSS contracts. Reports choose columns and grain as needed.
-- Catalog rows also qualify when contractnumber or cwinternalid appears in
-- pass_contract_mapping (agency_format or cw_format).
CREATE OR REPLACE VIEW pass_contract_dcss AS
SELECT *
FROM pass_contract con
WHERE pass_contract_fts_match(con.search_vector, 'DCSS')
   OR con.procurementmethoddescription = 'DC Supply Schedule'
   OR EXISTS (
        SELECT 1
        FROM pass_contract_mapping m
        WHERE upper(btrim(con.contractnumber)) IN (upper(btrim(m.agency_format)), upper(btrim(m.cw_format)))
           OR (
                con.cwinternalid IS NOT NULL
                AND btrim(con.cwinternalid) <> ''
                AND upper(btrim(con.cwinternalid)) IN (upper(btrim(m.agency_format)), upper(btrim(m.cw_format)))
           )
    );

-- The definition of DCSS purchase orders. Reports choose columns as needed.
-- Clean POs match on contractnumber (case-insensitive, trimmed).
-- "Messy" POs (slashes, extra text) match through po_contract_map.
-- Agency-format numbers in pass_contract_mapping match even when that number
-- is not a pass_contract row (see GAGA-2021-T-0190).
-- PASS sometimes stores the same PO as several rows that differ only by
-- agency label, each repeating the same total. Take only one row per PO (see PO459123).

-- POs that self-identify as DCSS (title or contract number) are included even
-- when there are no matching contract numbers.
DROP VIEW IF EXISTS purchase_order_dcss;
CREATE VIEW purchase_order_dcss AS
WITH dcss AS MATERIALIZED (
    SELECT DISTINCT upper(btrim(contractnumber)) AS cn_upper
    FROM pass_contract_dcss
    WHERE contractnumber IS NOT NULL
      AND btrim(contractnumber) <> ''

    UNION

    SELECT upper(btrim(agency_format))
    FROM pass_contract_mapping
    WHERE agency_format IS NOT NULL
      AND btrim(agency_format) <> ''

    UNION

    SELECT upper(btrim(cw_format))
    FROM pass_contract_mapping
    WHERE cw_format IS NOT NULL
      AND btrim(cw_format) <> ''
),
map_bases AS MATERIALIZED (
    SELECT DISTINCT m.ponumber_base
    FROM po_contract_map m
    JOIN dcss n ON m.contractnumber = n.cn_upper
),
matched AS (
    SELECT po.id
    FROM dcss n
    JOIN purchase_order po ON upper(btrim(po.contractnumber)) = n.cn_upper
    WHERE po.ponumber_base IS NOT NULL
      AND po.ponumber_base <> ''

    UNION

    SELECT po.id
    FROM map_bases mb
    JOIN purchase_order po ON po.ponumber_base = mb.ponumber_base

    UNION

    SELECT po.id
    FROM purchase_order po
    WHERE po.ponumber_base IS NOT NULL
      AND po.ponumber_base <> ''
      AND (po.potitle ~* 'dcss' OR po.contractnumber ~* 'dcss')
)
SELECT DISTINCT ON (po.ponumber_base) po.*
FROM purchase_order po
JOIN matched m ON m.id = po.id
ORDER BY po.ponumber_base, po.id;
