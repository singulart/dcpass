WITH po_by_fy AS (
    SELECT
        fiscalyear,
        SUM(pototal) AS po_total
    FROM purchase_order_dcss
    GROUP BY fiscalyear
),
pay_by_fy AS (
    SELECT
        pa.fiscalyear,
        SUM(pa.voucheramount) AS voucher_total
    FROM pass_payment pa
    WHERE pa.ponumber IN (SELECT ponumber_base FROM purchase_order_dcss)
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
