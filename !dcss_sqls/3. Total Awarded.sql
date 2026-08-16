-- Total Awarded DCSS Dollars (since the beginning of time)
WITH dcss AS MATERIALIZED (
  SELECT DISTINCT contractnumber,
         upper(btrim(contractnumber)) AS cn_upper
  FROM pass_contract_dcss
),
map_bases AS MATERIALIZED (
  SELECT DISTINCT m.ponumber_base
  FROM po_contract_map m
  JOIN dcss n ON m.contractnumber = n.cn_upper
),
matched AS (
  SELECT po.ponumber_base, po.pototal
  FROM dcss n
  JOIN purchase_order po ON po.contractnumber = n.contractnumber
  WHERE po.ponumber_base IS NOT NULL
    AND po.ponumber_base <> ''

  UNION ALL

  SELECT po.ponumber_base, po.pototal
  FROM map_bases mb
  JOIN purchase_order po ON po.ponumber_base = mb.ponumber_base
)
SELECT COALESCE(SUM(po_total), 0) AS total_task_order_dollars
FROM (
  SELECT ponumber_base, MAX(pototal) AS po_total
  FROM matched
  GROUP BY ponumber_base
) s;