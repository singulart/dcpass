-- DCSS catalog rows. Membership only; reports choose columns and grain.
CREATE OR REPLACE VIEW pass_contract_dcss AS
SELECT *
FROM pass_contract con
WHERE pass_contract_fts_match(con.search_vector, 'DCSS')
   OR con.procurementmethoddescription = 'DC Supply Schedule';

-- One purchase order per unversioned PO number (ponumber_base).
-- Clean POs match on contractnumber (case-insensitive, trimmed).
-- Messy POs (slashes, extra text) match through po_contract_map.
-- PASS sometimes stores the same PO as several rows that differ only by
-- agency label, each repeating the same pototal. Keep that amount once.
CREATE OR REPLACE VIEW purchase_order_dcss AS
WITH dcss AS MATERIALIZED (
    SELECT DISTINCT upper(btrim(contractnumber)) AS cn_upper
    FROM pass_contract_dcss
    WHERE contractnumber IS NOT NULL
      AND btrim(contractnumber) <> ''
),
map_bases AS MATERIALIZED (
    SELECT DISTINCT m.ponumber_base
    FROM po_contract_map m
    JOIN dcss n ON m.contractnumber = n.cn_upper
),
matched AS (
    SELECT po.ponumber_base, po.pototal, po.fiscalyear
    FROM dcss n
    JOIN purchase_order po ON upper(btrim(po.contractnumber)) = n.cn_upper
    WHERE po.ponumber_base IS NOT NULL
      AND po.ponumber_base <> ''

    UNION ALL

    SELECT po.ponumber_base, po.pototal, po.fiscalyear
    FROM map_bases mb
    JOIN purchase_order po ON po.ponumber_base = mb.ponumber_base
)
SELECT
    ponumber_base,
    MAX(pototal) AS pototal,
    MAX(fiscalyear) AS fiscalyear
FROM matched
GROUP BY ponumber_base;
