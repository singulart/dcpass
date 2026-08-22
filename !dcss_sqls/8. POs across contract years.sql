-- The value of this report is that it shows how bad the situation is with DCSS in terms of actual work.
-- OCP issues a lot of DCSS master agreements, but agencies don't put out task orders on them. 
WITH periods(currentoptionperiod, label) AS (
    VALUES
        ('Base Period', 'Base Year'),
        ('Option 1',    'Option 1'),
        ('Option 2',    'Option 2'),
        ('Option 3',    'Option 3'),
        ('Option 4',    'Option 4')
),
open_dcss AS (
    SELECT p.currentoptionperiod,
           p.label,
           btrim(c.contractnumber) AS cn,
           c.contracttypedescription
    FROM periods p
    JOIN pass_contract_dcss c
      ON c.currentoptionperiod = p.currentoptionperiod
    WHERE c.enddate >= now()
      AND c.contractstatus <> 'Closed'
      AND c.contractnumber IS NOT NULL
      AND btrim(c.contractnumber) <> ''
),
contract_counts AS (
    SELECT currentoptionperiod, label, count(DISTINCT cn) AS contracts
    FROM open_dcss
    WHERE contracttypedescription = 'Indefinite Delivery Indefinite Quantity'
    GROUP BY currentoptionperiod, label
),
period_query AS (
    SELECT currentoptionperiod, label, string_agg(DISTINCT cn, ' ') AS q
    FROM open_dcss
    GROUP BY currentoptionperiod, label
),
po_counts AS (
    SELECT pq.currentoptionperiod, pq.label, count(DISTINCT po.ponumber_base) AS pos
    FROM period_query pq
    JOIN purchase_order po
      ON purchase_order_fts_match(po.search_vector, pq.q)
    GROUP BY pq.currentoptionperiod, pq.label
),
by_period AS (
    SELECT
        p.label,
        coalesce(cc.contracts, 0) AS contracts,
        coalesce(pc.pos, 0) AS pos
    FROM periods p
    LEFT JOIN contract_counts cc ON cc.currentoptionperiod = p.currentoptionperiod
    LEFT JOIN po_counts pc ON pc.currentoptionperiod = p.currentoptionperiod
)
SELECT metric,
       max(val) FILTER (WHERE label = 'Base Year') AS "Base Year",
       max(val) FILTER (WHERE label = 'Option 1')  AS "Option 1",
       max(val) FILTER (WHERE label = 'Option 2')  AS "Option 2",
       max(val) FILTER (WHERE label = 'Option 3')  AS "Option 3",
       max(val) FILTER (WHERE label = 'Option 4')  AS "Option 4"
FROM (
    SELECT label, 'Contracts' AS metric, contracts AS val FROM by_period
    UNION ALL
    SELECT label, 'POs', pos FROM by_period
) u
GROUP BY metric
ORDER BY metric;