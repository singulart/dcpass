package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.repository.PassPaymentRepository;
import io.argorand.poc.dcpass.repository.PassPaymentRepository.PurchaseOrderPaymentAggregationResult;
import io.argorand.poc.dcpass.service.dto.PurchaseOrderPaymentSummaryDTO;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aggregates payment dollars for a purchase order by PO number.
 */
@Service
@Transactional(readOnly = true)
public class PurchaseOrderPaymentSummaryService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderPaymentSummaryService.class);

    private final PassPaymentRepository passPaymentRepository;

    public PurchaseOrderPaymentSummaryService(PassPaymentRepository passPaymentRepository) {
        this.passPaymentRepository = passPaymentRepository;
    }

    /**
     * Returns total dollars paid on payments matching {@code poNumber}.
     * Empty or blank PO numbers yield a zero summary.
     */
    public PurchaseOrderPaymentSummaryDTO getSummaryByPoNumber(String poNumber) {
        LOG.debug("Request to get payment summary for PO number : {}", poNumber);
        if (poNumber == null || poNumber.isBlank()) {
            return new PurchaseOrderPaymentSummaryDTO(poNumber, BigDecimal.ZERO, 0L);
        }
        String trimmed = poNumber.trim();
        PurchaseOrderPaymentAggregationResult result = passPaymentRepository.findPaidSummaryByPoNumber(trimmed);
        return new PurchaseOrderPaymentSummaryDTO(trimmed, result.totalPaid(), result.paymentCount());
    }
}
