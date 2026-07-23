package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Aggregated payment totals for a contract, summed over payments on POs issued under that contract.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContractPaymentSummaryDTO implements Serializable {

    private String contractNumber;
    private BigDecimal totalPaid;
    private long paymentCount;
    private long purchaseOrderCount;

    public ContractPaymentSummaryDTO() {}

    public ContractPaymentSummaryDTO(String contractNumber, BigDecimal totalPaid, long paymentCount, long purchaseOrderCount) {
        this.contractNumber = contractNumber;
        this.totalPaid = totalPaid;
        this.paymentCount = paymentCount;
        this.purchaseOrderCount = purchaseOrderCount;
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
            "}"
        );
    }
}
