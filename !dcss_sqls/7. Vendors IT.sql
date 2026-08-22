-- Same as 6. Vendors.sql, limited to POs whose commodity code is on the IT allowlist.
SELECT
    RANK() OVER (
        ORDER BY SUM(po.pototal) DESC, COUNT(*) DESC
    ) AS vendor_rank,
    COALESCE(NULLIF(btrim(po.supplier), ''), '(unknown)') AS vendor,
    COUNT(*) AS po_count,
    SUM(po.pototal) AS po_total
FROM purchase_order_dcss po
JOIN it_commodity_code icc
    ON replace(po.commoditycode, '-', '') ~ '^[0-9]+$'
   AND replace(po.commoditycode, '-', '')::bigint = icc.code
GROUP BY COALESCE(NULLIF(btrim(po.supplier), ''), '(unknown)')
HAVING SUM(po.pototal) > 250000
ORDER BY vendor_rank, vendor;
