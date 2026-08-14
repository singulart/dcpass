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
     * Sums {@code voucheramount} for payments whose {@code ponumber} matches any purchase order
     * issued under {@code contractNumber}. PASS payments store the unversioned PO number
     * ({@code PO123}); amended POs are stored as {@code PO123-V2}. Join on {@code ponumber_base}.
     * Distinct PO bases avoid double-counting multi-line POs.
     *
     * @return a single row: [purchaseOrderCount, paymentCount, totalPaid]
     */
    @Query(
        value = """
        SELECT
          COUNT(DISTINCT po.ponumber_base),
          COUNT(p.id),
          COALESCE(SUM(p.voucheramount), 0)
        FROM (
          SELECT DISTINCT ponumber_base
          FROM purchase_order
          WHERE contractnumber = :contractNumber
            AND ponumber_base IS NOT NULL
            AND ponumber_base <> ''
        ) po
        LEFT JOIN pass_payment p ON p.ponumber = po.ponumber_base
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
     * Sums {@code voucheramount} for payments whose {@code ponumber} matches {@code poNumber}
     * after stripping a trailing {@code -Vn} amendment suffix.
     *
     * @return a single row: [paymentCount, totalPaid]
     */
    @Query(
        value = """
        SELECT
          COUNT(p.id),
          COALESCE(SUM(p.voucheramount), 0)
        FROM pass_payment p
        WHERE p.ponumber = regexp_replace(:poNumber, '-V[0-9]+$', '')
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
