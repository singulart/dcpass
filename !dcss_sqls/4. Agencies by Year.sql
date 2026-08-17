-- Top Agencies by DCSS PO dollar value, by Year.  Shows agencies ordered $250,000 or more.
SELECT
    fiscalyear AS fiscal_year,
    RANK() OVER (
        PARTITION BY fiscalyear
        ORDER BY SUM(pototal) DESC, COUNT(*) DESC
    ) AS agency_rank,
    COALESCE(NULLIF(btrim(agency_acronym), ''), '(unknown)') AS agency_acronym,
    MAX(NULLIF(btrim(agency_name), '')) AS agency_name,
    COUNT(*) AS po_count,
    SUM(pototal) AS po_total
FROM purchase_order_dcss
GROUP BY fiscalyear, COALESCE(NULLIF(btrim(agency_acronym), ''), '(unknown)')
HAVING SUM(pototal) > 250000
ORDER BY fiscalyear NULLS LAST, agency_rank, agency_acronym;
