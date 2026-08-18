-- Materialize the DCSS PO set once. purchase_order_dcss is expensive, and
-- referencing it in both CTEs would evaluate the view twice.
WITH dcss_pos AS MATERIALIZED (
    SELECT fiscalyear, pototal, ponumber_base
    FROM purchase_order_dcss
),
po_by_fy AS (
    SELECT
        fiscalyear,
        SUM(pototal) AS po_total
    FROM dcss_pos
    GROUP BY fiscalyear
),
pay_by_fy AS (
    SELECT
        pa.fiscalyear,
        SUM(pa.voucheramount) AS voucher_total
    FROM pass_payment pa
    WHERE pa.ponumber IN (SELECT ponumber_base FROM dcss_pos)
    GROUP BY pa.fiscalyear
)
SELECT
    COALESCE(po.fiscalyear, pay.fiscalyear) AS fiscal_year,
    po.po_total,
    pay.voucher_total
FROM po_by_fy po
FULL OUTER JOIN pay_by_fy pay
    ON po.fiscalyear = pay.fiscalyear
ORDER BY fiscal_year;
