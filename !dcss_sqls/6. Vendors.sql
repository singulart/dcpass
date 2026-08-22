SELECT
    RANK() OVER (
        ORDER BY SUM(pototal) DESC, COUNT(*) DESC
    ) AS vendor_rank,
    COALESCE(NULLIF(btrim(supplier), ''), '(unknown)') AS vendor,
    COUNT(*) AS po_count,
    SUM(pototal) AS po_total
FROM purchase_order_dcss
GROUP BY COALESCE(NULLIF(btrim(supplier), ''), '(unknown)')
HAVING SUM(pototal) > 250000
ORDER BY vendor_rank, vendor;
