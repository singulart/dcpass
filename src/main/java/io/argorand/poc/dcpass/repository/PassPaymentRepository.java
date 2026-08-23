package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.PassPayment;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PassPayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PassPaymentRepository
    extends JpaRepository<PassPayment, Long>, JpaSpecificationExecutor<PassPayment>, PassPaymentRepositoryCustom
{
    /**
     * Sums {@code voucheramount} for payments whose {@code ponumber} matches any purchase order
     * issued under {@code contractNumber} or a related contract number.
     * <p>
     * Related numbers are {@code pass_contract.contractnumber} rows whose {@code cwinternalid}
     * equals this contract number. POs are found with one GIN {@code purchase_order_fts_match}
     * against those numbers (space-joined so {@code dcpass_fts_query} ORs them: slash-split
     * and CW-digit tokens in {@code search_vector} match without an equality join), plus POs
     * linked through {@code po_contract_map} (agency-format numbers that are not on the PO
     * row). PASS payments store the unversioned PO number ({@code PO123}); amended POs are
     * stored as {@code PO123-V2}. Join on {@code ponumber_base}. Distinct PO bases avoid
     * double-counting multi-line POs.
     * <p>
     * Native SQL (and JIT-off) lives in {@link PassPaymentRepositoryImpl}.
     */
    default ContractPaymentAggregationResult findPaidSummaryByContractNumber(String contractNumber) {
        List<Object[]> rows = aggregatePaidByContractNumber(contractNumber);
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return new ContractPaymentAggregationResult(0L, 0L, BigDecimal.ZERO, List.of(), List.of());
        }
        Object[] row = rows.get(0);
        long purchaseOrderCount = row[0] == null ? 0L : ((Number) row[0]).longValue();
        long paymentCount = row[1] == null ? 0L : ((Number) row[1]).longValue();
        BigDecimal totalPaid = row[2] == null ? BigDecimal.ZERO : new BigDecimal(row[2].toString());
        List<String> poNumbers = row.length > 3 ? splitCsv(row[3]) : List.of();
        List<String> paymentNumbers = row.length > 4 ? splitCsv(row[4]) : List.of();
        return new ContractPaymentAggregationResult(purchaseOrderCount, paymentCount, totalPaid, poNumbers, paymentNumbers);
    }

    private static List<String> splitCsv(Object value) {
        if (value == null) {
            return List.of();
        }
        String raw = value.toString().trim();
        if (raw.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(raw.split(","))
            .map(String::trim)
            .filter(part -> !part.isEmpty())
            .distinct()
            .toList();
    }

    record ContractPaymentAggregationResult(
        long purchaseOrderCount,
        long paymentCount,
        BigDecimal totalPaid,
        List<String> poNumbers,
        List<String> paymentNumbers
    ) {}

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
