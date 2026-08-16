WITH po_by_fy AS (
    SELECT
        po.fiscalyear,
        SUM(po.pototal) AS po_total
    FROM purchase_order po
    WHERE po.contractnumber IN (SELECT contractnumber FROM pass_contract_dcss)
    GROUP BY po.fiscalyear
),
pay_by_fy AS (
    SELECT
        pa.fiscalyear,
        SUM(pa.voucheramount) AS voucher_total
    FROM pass_payment pa
    WHERE pa.ponumber IN (
        SELECT po.ponumber_base
        FROM purchase_order po
        WHERE po.contractnumber IN (SELECT contractnumber FROM pass_contract_dcss)
    )
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