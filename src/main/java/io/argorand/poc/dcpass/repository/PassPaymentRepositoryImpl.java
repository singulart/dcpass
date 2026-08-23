package io.argorand.poc.dcpass.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs the contract payment-summary query with {@code jit=off}.
 * <p>
 * The planner estimates this statement at ~550k cost (opaque FTS selectivity plus a PO-number
 * subquery hashed against all purchase orders), which is above {@code jit_optimize_above_cost}
 * (500k). LLVM then spends ~500–600ms compiling a plan whose useful work is a GIN probe of a
 * few milliseconds. {@code SET LOCAL jit = off} in the same transaction avoids that.
 */
@Repository
public class PassPaymentRepositoryImpl implements PassPaymentRepositoryCustom {

    private static final String PAID_BY_CONTRACT_SQL = """
        WITH related_numbers AS (
          SELECT :contractNumber AS contractnumber
          UNION
          SELECT c.contractnumber
          FROM pass_contract c
          WHERE c.cwinternalid = :contractNumber
            AND c.contractnumber IS NOT NULL
            AND btrim(c.contractnumber) <> ''
        ),
        fts_query AS (
          SELECT string_agg(DISTINCT btrim(contractnumber), ' ') AS q
          FROM related_numbers
        ),
        matching_pos AS (
          SELECT po.ponumber_base
          FROM purchase_order po
          CROSS JOIN fts_query
          WHERE fts_query.q IS NOT NULL
            AND purchase_order_fts_match(po.search_vector, fts_query.q)
            AND po.ponumber_base IS NOT NULL
            AND po.ponumber_base <> ''
            AND po.status IS DISTINCT FROM 'Canceled'
            AND po.status IS DISTINCT FROM 'Cancelling'
          UNION
          SELECT m.ponumber_base
          FROM related_numbers n
          JOIN po_contract_map m ON m.contractnumber = upper(n.contractnumber)
          WHERE m.ponumber_base IS NOT NULL
            AND m.ponumber_base <> ''
            AND EXISTS (
              SELECT 1
              FROM purchase_order po
              WHERE po.ponumber_base = m.ponumber_base
                AND po.status IS DISTINCT FROM 'Canceled'
                AND po.status IS DISTINCT FROM 'Cancelling'
            )
        )
        SELECT
          COUNT(DISTINCT matching_pos.ponumber_base),
          COUNT(p.id),
          COALESCE(SUM(p.voucheramount), 0),
          (
            SELECT string_agg(DISTINCT po.ponumber, ',' ORDER BY po.ponumber)
            FROM matching_pos m
            JOIN LATERAL (
              SELECT po.ponumber
              FROM purchase_order po
              WHERE po.ponumber_base = m.ponumber_base
                AND po.ponumber IS NOT NULL
                AND btrim(po.ponumber) <> ''
            ) po ON true
          ),
          string_agg(DISTINCT p.paymentnumber, ',' ORDER BY p.paymentnumber)
            FILTER (WHERE p.paymentnumber IS NOT NULL AND btrim(p.paymentnumber) <> '')
        FROM matching_pos
        LEFT JOIN pass_payment p ON p.ponumber = matching_pos.ponumber_base
        """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Object[]> aggregatePaidByContractNumber(String contractNumber) {
        entityManager.createNativeQuery("SELECT set_config('jit', 'off', true)").getSingleResult();
        Query query = entityManager.createNativeQuery(PAID_BY_CONTRACT_SQL);
        query.setParameter("contractNumber", contractNumber);
        return query.getResultList();
    }
}
