package io.argorand.poc.dcpass.repository;

import java.util.List;

/**
 * Native aggregation that must run with JIT disabled (see {@link PassPaymentRepositoryImpl}).
 */
public interface PassPaymentRepositoryCustom {
    /**
     * @return a single row: [purchaseOrderCount, paymentCount, totalPaid, poNumbersCsv, paymentNumbersCsv]
     */
    List<Object[]> aggregatePaidByContractNumber(String contractNumber);
}
