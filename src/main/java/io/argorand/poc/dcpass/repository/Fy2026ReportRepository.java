package io.argorand.poc.dcpass.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Native report queries for FY2026 IT spend drill-down charts.
 * Extends {@link PassPayment} repository surface only for Spring Data wiring;
 * all methods are native SQL aggregations.
 */
@Repository
public interface Fy2026ReportRepository extends JpaRepository<io.argorand.poc.dcpass.domain.PassPayment, Long> {
    @Query(
        value = """
        SELECT "Agency", "Spend"
        FROM mv_fy2026_it_spend_by_agency
        ORDER BY "Spend" DESC
        """,
        nativeQuery = true
    )
    List<Object[]> findAgencySpend();

    @Query(
        value = """
        SELECT
          COALESCE(
            (SELECT DISTINCT c.title FROM pass_contract c WHERE c.contractnumber = po.contractnumber),
            'NO CONTRACT'
          ),
          po.contractnumber,
          SUM(pp.paymentamount)
        FROM pass_payment pp
        JOIN purchase_order po ON pp.ponumber = po.ponumber
        JOIN it_commodity_code icc ON po.commoditycode ~ '^[0-9]+$' AND po.commoditycode::bigint = icc.code
        WHERE pp.fiscalyear = 2026
          AND po.agency_acronym = :agencyAcronym
        GROUP BY 1, 2
        ORDER BY 3 DESC
        """,
        nativeQuery = true
    )
    List<Object[]> findContractSpendByAgency(@Param("agencyAcronym") String agencyAcronym);

    @Query(
        value = """
        SELECT
          MIN(po.id),
          po.ponumber,
          po.potitle,
          SUM(pp.paymentamount)
        FROM pass_payment pp
        JOIN purchase_order po ON pp.ponumber = po.ponumber
        JOIN it_commodity_code icc ON po.commoditycode ~ '^[0-9]+$' AND po.commoditycode::bigint = icc.code
        WHERE pp.fiscalyear = 2026
          AND po.agency_acronym = :agencyAcronym
          AND po.contractnumber = :contractNumber
        GROUP BY 2, 3
        ORDER BY 4 DESC
        """,
        nativeQuery = true
    )
    List<Object[]> findPoSpendByAgencyAndContract(
        @Param("agencyAcronym") String agencyAcronym,
        @Param("contractNumber") String contractNumber
    );

    @Query(
        value = """
        SELECT
          MIN(po.id),
          po.ponumber,
          po.potitle,
          SUM(pp.paymentamount)
        FROM pass_payment pp
        JOIN purchase_order po ON pp.ponumber = po.ponumber
        JOIN it_commodity_code icc ON po.commoditycode ~ '^[0-9]+$' AND po.commoditycode::bigint = icc.code
        WHERE pp.fiscalyear = 2026
          AND po.agency_acronym = :agencyAcronym
          AND po.contractnumber IS NULL
        GROUP BY 2, 3
        ORDER BY 4 DESC
        """,
        nativeQuery = true
    )
    List<Object[]> findPoSpendByAgencyAndNullContract(@Param("agencyAcronym") String agencyAcronym);

    default List<AgencySpendRow> mapAgencySpend() {
        List<AgencySpendRow> result = new ArrayList<>();
        for (Object[] row : findAgencySpend()) {
            String agencyRaw = row[0] == null ? "" : row[0].toString();
            BigDecimal spend = row[1] == null ? BigDecimal.ZERO : new BigDecimal(row[1].toString());
            result.add(new AgencySpendRow(agencyRaw, spend));
        }
        return result;
    }

    default List<ContractSpendRow> mapContractSpend(String agencyAcronym) {
        List<ContractSpendRow> result = new ArrayList<>();
        for (Object[] row : findContractSpendByAgency(agencyAcronym)) {
            String title = row[0] == null ? "NO CONTRACT" : row[0].toString();
            String contractNumber = row[1] == null ? null : row[1].toString();
            BigDecimal spend = row[2] == null ? BigDecimal.ZERO : new BigDecimal(row[2].toString());
            result.add(new ContractSpendRow(title, contractNumber, spend));
        }
        return result;
    }

    default List<PoSpendRow> mapPoSpend(String agencyAcronym, String contractNumber) {
        List<Object[]> rows =
            contractNumber == null
                ? findPoSpendByAgencyAndNullContract(agencyAcronym)
                : findPoSpendByAgencyAndContract(agencyAcronym, contractNumber);
        List<PoSpendRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            Long purchaseOrderId = row[0] == null ? null : ((Number) row[0]).longValue();
            String poNumber = row[1] == null ? null : row[1].toString();
            String poTitle = row[2] == null ? null : row[2].toString();
            BigDecimal spend = row[3] == null ? BigDecimal.ZERO : new BigDecimal(row[3].toString());
            result.add(new PoSpendRow(purchaseOrderId, poNumber, poTitle, spend));
        }
        return result;
    }

    record AgencySpendRow(String agencyRaw, BigDecimal spend) {}

    record ContractSpendRow(String contractTitle, String contractNumber, BigDecimal spend) {}

    record PoSpendRow(Long purchaseOrderId, String poNumber, String poTitle, BigDecimal spend) {}
}
