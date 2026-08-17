-- Local-only attribution job. Do not run on production.
-- This table is needed for prod: copy manually from local DB.
--
-- Parses messy purchase_order.contractnumber values (slash, brace, colon, space).
--
--   psql -h localhost -U dcpass -d dcpass -f !data_cleanup/refresh_po_contract_map.sql

CREATE OR REPLACE FUNCTION refresh_po_contract_map() RETURNS integer
LANGUAGE plpgsql AS $fn$
DECLARE
    inserted integer;
BEGIN
    DELETE FROM po_contract_map WHERE strategy = 'messy_token';

    INSERT INTO po_contract_map (ponumber_base, contractnumber, strategy)
    WITH messy AS (
        SELECT DISTINCT ponumber_base, contractnumber AS source
        FROM purchase_order
        WHERE contractnumber ~ '[/{}: ()]'
          AND ponumber_base IS NOT NULL
          AND ponumber_base <> ''
          AND btrim(contractnumber) <> ''
    ),
    regex_hits AS (
        SELECT m.ponumber_base, upper(x[1]) AS token
        FROM messy m,
        LATERAL regexp_matches(
            m.source,
            '(?i)(CW[0-9]{4,8}|[A-Z0-9]{2,10}-[0-9]{2,4}(?:-[A-Z0-9]+)+|GS-[0-9]{1,3}[A-Z]?-[0-9A-Z]+|\mC[0-9]{3,6}(?:-V[0-9]+)?\M|[0-9]{2}[A-Z]{4}[0-9]{2}[A-Z][0-9A-Z]{3,}|\m[0-9]{4}-[0-9]{1,3}\M)',
            'gi'
        ) AS x
    ),
    slash_parts AS (
        SELECT
            m.ponumber_base,
            trim(both ' -_.,;:#+*' FROM
                CASE
                    WHEN btrim(part) ~ '\s'
                        THEN split_part(regexp_replace(btrim(part), '\s+', ' ', 'g'), ' ', 1)
                    ELSE btrim(part)
                END
            ) AS token
        FROM messy m,
        LATERAL unnest(regexp_split_to_array(
            regexp_replace(m.source, '\([^)]*\)', ' ', 'g'),
            '/'
        )) AS part
    )
    SELECT DISTINCT ponumber_base, upper(token), 'messy_token'
    FROM (
        SELECT ponumber_base, token FROM regex_hits
        UNION ALL
        SELECT ponumber_base, token FROM slash_parts
    ) u
    WHERE token ~ '[0-9]'
      AND length(token) BETWEEN 4 AND 40
      AND token ~ '^[A-Z0-9]([A-Z0-9._-]*[A-Z0-9])?$'
      AND token !~ '^(19|20)[0-9]{2}$'
      AND token !~ '^(M[0-9]{1,5}|TO[0-9]{1,3}|NA|NONE|NULL|N/A|FY[0-9]{2,4})$'
    ON CONFLICT DO NOTHING;

    GET DIAGNOSTICS inserted = ROW_COUNT;
    RETURN inserted;
END;
$fn$;

SELECT refresh_po_contract_map();
