package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.repository.PassPaymentRepository;
import io.argorand.poc.dcpass.repository.PassPaymentRepository.ContractPaymentAggregationResult;
import io.argorand.poc.dcpass.service.dto.ContractPaymentSummaryDTO;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aggregates payment dollars for a contract via its purchase orders.
 */
@Service
@Transactional(readOnly = true)
public class ContractPaymentSummaryService {

    private static final Logger LOG = LoggerFactory.getLogger(ContractPaymentSummaryService.class);

    private final PassPaymentRepository passPaymentRepository;

    public ContractPaymentSummaryService(PassPaymentRepository passPaymentRepository) {
        this.passPaymentRepository = passPaymentRepository;
    }

    /**
     * Returns total dollars paid on any PO issued for {@code contractNumber}.
     * Empty or blank contract numbers yield a zero summary.
     */
    public ContractPaymentSummaryDTO getSummaryByContractNumber(String contractNumber) {
        LOG.debug("Request to get payment summary for contract number : {}", contractNumber);
        if (contractNumber == null || contractNumber.isBlank()) {
            return new ContractPaymentSummaryDTO(contractNumber, BigDecimal.ZERO, 0L, 0L);
        }
        String trimmed = contractNumber.trim();
        ContractPaymentAggregationResult result = passPaymentRepository.findPaidSummaryByContractNumber(trimmed);
        return new ContractPaymentSummaryDTO(trimmed, result.totalPaid(), result.paymentCount(), result.purchaseOrderCount());
    }
}
