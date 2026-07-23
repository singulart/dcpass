package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.argorand.poc.dcpass.domain.PassPayment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PassPaymentDTO implements Serializable {

    private Long id;
    private String agencyCode;
    private String agencyAcronym;
    private String agencyName;
    private String contractNumber;
    private String supplierName;
    private String invoiceNumber;
    private String poNumber;
    private String voucherNumber;
    private Instant paymentDate;
    private BigDecimal paymentAmount;
    private Integer fiscalYear;
    private String transactionCode;
    private String paymentType;
    private Instant invoiceDate;
    private Instant estPaymentDate;
    private String paymentNumber;
    private Instant recordUpdatedDate;
    private Instant recordCreated;
    private Instant dcsRecCrtDttm;
    private Instant dcsLastModDttm;
    private Long objectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getAgencyAcronym() {
        return agencyAcronym;
    }

    public void setAgencyAcronym(String agencyAcronym) {
        this.agencyAcronym = agencyAcronym;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Instant getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Instant invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Instant getEstPaymentDate() {
        return estPaymentDate;
    }

    public void setEstPaymentDate(Instant estPaymentDate) {
        this.estPaymentDate = estPaymentDate;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public Instant getRecordUpdatedDate() {
        return recordUpdatedDate;
    }

    public void setRecordUpdatedDate(Instant recordUpdatedDate) {
        this.recordUpdatedDate = recordUpdatedDate;
    }

    public Instant getRecordCreated() {
        return recordCreated;
    }

    public void setRecordCreated(Instant recordCreated) {
        this.recordCreated = recordCreated;
    }

    public Instant getDcsRecCrtDttm() {
        return dcsRecCrtDttm;
    }

    public void setDcsRecCrtDttm(Instant dcsRecCrtDttm) {
        this.dcsRecCrtDttm = dcsRecCrtDttm;
    }

    public Instant getDcsLastModDttm() {
        return dcsLastModDttm;
    }

    public void setDcsLastModDttm(Instant dcsLastModDttm) {
        this.dcsLastModDttm = dcsLastModDttm;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PassPaymentDTO)) {
            return false;
        }

        PassPaymentDTO passPaymentDTO = (PassPaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, passPaymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PassPaymentDTO{" +
            "id=" + getId() +
            ", agencyCode='" + getAgencyCode() + "'" +
            ", agencyAcronym='" + getAgencyAcronym() + "'" +
            ", agencyName='" + getAgencyName() + "'" +
            ", contractNumber='" + getContractNumber() + "'" +
            ", supplierName='" + getSupplierName() + "'" +
            ", invoiceNumber='" + getInvoiceNumber() + "'" +
            ", poNumber='" + getPoNumber() + "'" +
            ", voucherNumber='" + getVoucherNumber() + "'" +
            ", paymentDate=" + getPaymentDate() +
            ", paymentAmount=" + getPaymentAmount() +
            ", fiscalYear=" + getFiscalYear() +
            ", transactionCode='" + getTransactionCode() + "'" +
            ", paymentType='" + getPaymentType() + "'" +
            ", invoiceDate=" + getInvoiceDate() +
            ", estPaymentDate=" + getEstPaymentDate() +
            ", paymentNumber='" + getPaymentNumber() + "'" +
            ", recordUpdatedDate=" + getRecordUpdatedDate() +
            ", recordCreated=" + getRecordCreated() +
            ", dcsRecCrtDttm=" + getDcsRecCrtDttm() +
            ", dcsLastModDttm=" + getDcsLastModDttm() +
            ", objectId=" + getObjectId() +
            "}";
    }
}
