SELECT
    fiscalyear AS fiscal_year,
    RANK() OVER (
        PARTITION BY fiscalyear
        ORDER BY SUM(pototal) DESC, COUNT(*) DESC
    ) AS vendor_rank,
    COALESCE(NULLIF(btrim(supplier), ''), '(unknown)') AS vendor,
    COUNT(*) AS po_count,
    SUM(pototal) AS po_total
FROM purchase_order_dcss
GROUP BY fiscalyear, COALESCE(NULLIF(btrim(supplier), ''), '(unknown)')
HAVING SUM(pototal) > 250000
ORDER BY fiscalyear NULLS LAST, vendor_rank, vendor;
