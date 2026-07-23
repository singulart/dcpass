package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Aggregated payment totals for a purchase order, summed over payments matching that PO number.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrderPaymentSummaryDTO implements Serializable {

    private String poNumber;
    private BigDecimal totalPaid;
    private long paymentCount;

    public PurchaseOrderPaymentSummaryDTO() {}

    public PurchaseOrderPaymentSummaryDTO(String poNumber, BigDecimal totalPaid, long paymentCount) {
        this.poNumber = poNumber;
        this.totalPaid = totalPaid;
        this.paymentCount = paymentCount;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOrderPaymentSummaryDTO that)) {
            return false;
        }
        return Objects.equals(poNumber, that.poNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poNumber);
    }

    @Override
    public String toString() {
        return (
            "PurchaseOrderPaymentSummaryDTO{" +
            "poNumber='" +
            poNumber +
            "'" +
            ", totalPaid=" +
            totalPaid +
            ", paymentCount=" +
            paymentCount +
            "}"
        );
    }
}
