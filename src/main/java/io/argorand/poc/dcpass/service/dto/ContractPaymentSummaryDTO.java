package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregated payment totals for a contract, summed over payments on POs issued under that
 * contract, related contract numbers, or {@code po_contract_map} links.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContractPaymentSummaryDTO implements Serializable {

    private String contractNumber;
    private BigDecimal totalPaid;
    private long paymentCount;
    private long purchaseOrderCount;
    private List<String> poNumbers = new ArrayList<>();
    private List<String> paymentNumbers = new ArrayList<>();

    public ContractPaymentSummaryDTO() {}

    public ContractPaymentSummaryDTO(String contractNumber, BigDecimal totalPaid, long paymentCount, long purchaseOrderCount) {
        this(contractNumber, totalPaid, paymentCount, purchaseOrderCount, List.of(), List.of());
    }

    public ContractPaymentSummaryDTO(
        String contractNumber,
        BigDecimal totalPaid,
        long paymentCount,
        long purchaseOrderCount,
        List<String> poNumbers,
        List<String> paymentNumbers
    ) {
        this.contractNumber = contractNumber;
        this.totalPaid = totalPaid;
        this.paymentCount = paymentCount;
        this.purchaseOrderCount = purchaseOrderCount;
        this.poNumbers = poNumbers == null ? new ArrayList<>() : new ArrayList<>(poNumbers);
        this.paymentNumbers = paymentNumbers == null ? new ArrayList<>() : new ArrayList<>(paymentNumbers);
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public long getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(long paymentCount) {
        this.paymentCount = paymentCount;
    }

    public long getPurchaseOrderCount() {
        return purchaseOrderCount;
    }

    public void setPurchaseOrderCount(long purchaseOrderCount) {
        this.purchaseOrderCount = purchaseOrderCount;
    }

    public List<String> getPoNumbers() {
        return poNumbers;
    }

    public void setPoNumbers(List<String> poNumbers) {
        this.poNumbers = poNumbers == null ? new ArrayList<>() : poNumbers;
    }

    public List<String> getPaymentNumbers() {
        return paymentNumbers;
    }

    public void setPaymentNumbers(List<String> paymentNumbers) {
        this.paymentNumbers = paymentNumbers == null ? new ArrayList<>() : paymentNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContractPaymentSummaryDTO that)) {
            return false;
        }
        return Objects.equals(contractNumber, that.contractNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractNumber);
    }

    @Override
    public String toString() {
        return (
            "ContractPaymentSummaryDTO{" +
            "contractNumber='" +
            contractNumber +
            "'" +
            ", totalPaid=" +
            totalPaid +
            ", paymentCount=" +
            paymentCount +
            ", purchaseOrderCount=" +
            purchaseOrderCount +
            ", poNumbers=" +
            poNumbers +
            ", paymentNumbers=" +
            paymentNumbers +
            "}"
        );
    }
}
