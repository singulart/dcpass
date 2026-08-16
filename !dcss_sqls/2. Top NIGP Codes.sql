SELECT
    COUNT(DISTINCT contractnumber) AS cnt,
    replace(commoditycode, '-', '') AS nigp_code,
    commoditydescription AS nigp_description
FROM pass_contract_dcss
WHERE commoditycode IS NOT NULL AND commoditycode <> ''
GROUP BY replace(commoditycode, '-', ''), 
         nigp_description
ORDER BY cnt DESC, nigp_code;