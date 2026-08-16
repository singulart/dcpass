SELECT
    commoditycode,
    COUNT(*) AS cnt
FROM pass_contract_dcss
WHERE commoditycode IS NOT NULL AND commoditycode <> ''
GROUP BY commoditycode
ORDER BY cnt DESC, commoditycode;