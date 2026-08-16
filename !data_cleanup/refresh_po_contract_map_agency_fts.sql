-- Local-only attribution job. Do not run on production.
-- Prod receives po_contract_map by table copy after this has been applied locally.
--
-- FTS + date/FY for clean DC-agency numbers. Rebuilds only strategy = 'agency_fts' rows.
-- Requires po_contract_map.strategy (Liquibase 20260815200000-1).
--
--   psql -h localhost -U dcpass -d dcpass -f !data_cleanup/refresh_po_contract_map_agency_fts.sql

CREATE OR REPLACE FUNCTION refresh_po_contract_map_agency_fts() RETURNS integer
LANGUAGE plpgsql AS $fn$
DECLARE
    inserted integer;
BEGIN
    DELETE FROM po_contract_map WHERE strategy = 'agency_fts';

    INSERT INTO po_contract_map (ponumber_base, contractnumber, strategy)
    WITH contract_keys AS (
        SELECT DISTINCT k
        FROM (
            SELECT btrim(contractnumber) AS k
            FROM pass_contract
            WHERE contractnumber IS NOT NULL AND btrim(contractnumber) <> ''
            UNION ALL
            SELECT btrim(cwinternalid)
            FROM pass_contract
            WHERE cwinternalid IS NOT NULL AND btrim(cwinternalid) <> ''
        ) u
    ),
    agency_pos AS (
        SELECT
            po.ponumber_base,
            upper(btrim(po.contractnumber)) AS agency_num,
            MIN(po.fiscalyear) AS po_fy,
            MIN((COALESCE(po.ordereddate, po.createdate))::date) AS po_date
        FROM purchase_order po
        LEFT JOIN contract_keys ck ON ck.k = po.contractnumber
        WHERE po.ponumber_base IS NOT NULL
          AND po.ponumber_base <> ''
          AND po.contractnumber IS NOT NULL
          AND btrim(po.contractnumber) <> ''
          AND po.contractnumber ~* '^[A-Z0-9]{2,10}-[0-9]{2,4}(-[A-Z0-9]+)+$'
          AND ck.k IS NULL
        GROUP BY po.ponumber_base, upper(btrim(po.contractnumber))
    ),
    agency_nums AS (
        SELECT DISTINCT agency_num FROM agency_pos
    ),
    fts_hits AS (
        SELECT
            n.agency_num,
            btrim(c.contractnumber) AS catalog_cn,
            c.startdate,
            c.enddate,
            c.fiscalyear AS contract_fy
        FROM agency_nums n
        JOIN pass_contract c
          ON c.search_vector @@ dcpass_fts_query(n.agency_num)
        WHERE c.contractnumber IS NOT NULL
          AND btrim(c.contractnumber) <> ''
    ),
    unique_num_map AS (
        SELECT agency_num, MIN(catalog_cn) AS catalog_cn
        FROM fts_hits
        GROUP BY agency_num
        HAVING COUNT(DISTINCT catalog_cn) = 1
    ),
    ambiguous_pos AS (
        SELECT p.*
        FROM agency_pos p
        LEFT JOIN unique_num_map u ON u.agency_num = p.agency_num
        WHERE u.agency_num IS NULL
    ),
    amb_hits AS (
        SELECT
            p.ponumber_base,
            h.catalog_cn,
            (
                p.po_date IS NOT NULL
                AND h.startdate IS NOT NULL
                AND h.enddate IS NOT NULL
                AND p.po_date BETWEEN h.startdate AND h.enddate
            ) AS date_ok,
            (
                p.po_fy IS NOT NULL
                AND h.contract_fy IS NOT NULL
                AND p.po_fy = h.contract_fy
            ) AS fy_ok
        FROM ambiguous_pos p
        JOIN fts_hits h ON h.agency_num = p.agency_num
    ),
    date_unique AS (
        SELECT ponumber_base
        FROM amb_hits
        WHERE date_ok
        GROUP BY ponumber_base
        HAVING COUNT(DISTINCT catalog_cn) = 1
    ),
    fy_unique AS (
        SELECT h.ponumber_base
        FROM amb_hits h
        LEFT JOIN date_unique d ON d.ponumber_base = h.ponumber_base
        WHERE d.ponumber_base IS NULL
          AND h.fy_ok
        GROUP BY h.ponumber_base
        HAVING COUNT(DISTINCT catalog_cn) = 1
    ),
    picked AS (
        SELECT p.ponumber_base, u.catalog_cn
        FROM agency_pos p
        JOIN unique_num_map u ON u.agency_num = p.agency_num
        UNION ALL
        SELECT h.ponumber_base, MIN(h.catalog_cn) AS catalog_cn
        FROM amb_hits h
        JOIN date_unique d ON d.ponumber_base = h.ponumber_base
        WHERE h.date_ok
        GROUP BY h.ponumber_base
        UNION ALL
        SELECT h.ponumber_base, MIN(h.catalog_cn) AS catalog_cn
        FROM amb_hits h
        JOIN fy_unique f ON f.ponumber_base = h.ponumber_base
        WHERE h.fy_ok
        GROUP BY h.ponumber_base
    )
    SELECT DISTINCT ponumber_base, upper(catalog_cn), 'agency_fts'
    FROM picked
    WHERE catalog_cn IS NOT NULL
      AND btrim(catalog_cn) <> ''
    ON CONFLICT DO NOTHING;

    GET DIAGNOSTICS inserted = ROW_COUNT;
    RAISE NOTICE 'refresh_po_contract_map_agency_fts: inserted=%', inserted;
    RETURN inserted;
END;
$fn$;

SELECT refresh_po_contract_map_agency_fts();
