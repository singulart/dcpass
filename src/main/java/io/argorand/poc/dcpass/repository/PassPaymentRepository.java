package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.PassPayment;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PassPayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PassPaymentRepository extends JpaRepository<PassPayment, Long>, JpaSpecificationExecutor<PassPayment> {
    /**
     * Sums {@code paymentamount} for payments whose {@code ponumber} appears on any purchase order
     * issued under {@code contractNumber}. Distinct PO numbers avoid double-counting multi-line POs.
     *
     * @return a single row: [purchaseOrderCount, paymentCount, totalPaid]
     */
    @Query(
        value = """
        SELECT
          COUNT(DISTINCT po.ponumber),
          COUNT(p.id),
          COALESCE(SUM(p.paymentamount), 0)
        FROM (
          SELECT DISTINCT ponumber
          FROM purchase_order
          WHERE contractnumber = :contractNumber
            AND ponumber IS NOT NULL
            AND ponumber <> ''
        ) po
        LEFT JOIN pass_payment p ON p.ponumber = po.ponumber
        """,
        nativeQuery = true
    )
    List<Object[]> aggregatePaidByContractNumber(@Param("contractNumber") String contractNumber);

    default ContractPaymentAggregationResult findPaidSummaryByContractNumber(String contractNumber) {
        List<Object[]> rows = aggregatePaidByContractNumber(contractNumber);
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return new ContractPaymentAggregationResult(0L, 0L, BigDecimal.ZERO);
        }
        Object[] row = rows.get(0);
        long purchaseOrderCount = row[0] == null ? 0L : ((Number) row[0]).longValue();
        long paymentCount = row[1] == null ? 0L : ((Number) row[1]).longValue();
        BigDecimal totalPaid = row[2] == null ? BigDecimal.ZERO : new BigDecimal(row[2].toString());
        return new ContractPaymentAggregationResult(purchaseOrderCount, paymentCount, totalPaid);
    }

    record ContractPaymentAggregationResult(long purchaseOrderCount, long paymentCount, BigDecimal totalPaid) {}

    /**
     * Sums {@code paymentamount} for payments whose {@code ponumber} matches {@code poNumber}.
     *
     * @return a single row: [paymentCount, totalPaid]
     */
    @Query(
        value = """
        SELECT
          COUNT(p.id),
          COALESCE(SUM(p.paymentamount), 0)
        FROM pass_payment p
        WHERE p.ponumber = :poNumber
        """,
        nativeQuery = true
    )
    List<Object[]> aggregatePaidByPoNumber(@Param("poNumber") String poNumber);

    default PurchaseOrderPaymentAggregationResult findPaidSummaryByPoNumber(String poNumber) {
        List<Object[]> rows = aggregatePaidByPoNumber(poNumber);
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return new PurchaseOrderPaymentAggregationResult(0L, BigDecimal.ZERO);
        }
        Object[] row = rows.get(0);
        long paymentCount = row[0] == null ? 0L : ((Number) row[0]).longValue();
        BigDecimal totalPaid = row[1] == null ? BigDecimal.ZERO : new BigDecimal(row[1].toString());
        return new PurchaseOrderPaymentAggregationResult(paymentCount, totalPaid);
    }

    record PurchaseOrderPaymentAggregationResult(long paymentCount, BigDecimal totalPaid) {}
}
