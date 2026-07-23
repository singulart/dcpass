package io.argorand.poc.dcpass.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PassPayment.
 */
@Entity
@Table(name = "pass_payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PassPayment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "pass_payment_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "AGENCYCODE")
    private String agencyCode;

    @Column(name = "AGENCY_ACRONYM")
    private String agencyAcronym;

    @Column(name = "AGENCY_NAME")
    private String agencyName;

    @Column(name = "CONTRACTNUMBER")
    private String contractNumber;

    @Column(name = "SUPPLIERNAME")
    private String supplierName;

    @Column(name = "INVOICENUMBER")
    private String invoiceNumber;

    @Column(name = "PONUMBER")
    private String poNumber;

    @Column(name = "VOUCHERNUMBER")
    private String voucherNumber;

    @Column(name = "PAYMENTDATE")
    private Instant paymentDate;

    @Column(name = "PAYMENTAMOUNT", precision = 21, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "FISCALYEAR")
    private Integer fiscalYear;

    @Column(name = "TRANSACTION_CODE")
    private String transactionCode;

    @Column(name = "PAYMENTTYPE")
    private String paymentType;

    @Column(name = "INVOICEDATE")
    private Instant invoiceDate;

    @Column(name = "ESTPAYMENTDATE")
    private Instant estPaymentDate;

    @Column(name = "PAYMENTNUMBER")
    private String paymentNumber;

    @Column(name = "RECORDUPDATEDDATE")
    private Instant recordUpdatedDate;

    @Column(name = "RECORDCREATED")
    private Instant recordCreated;

    @Column(name = "DCS_REC_CRT_DTTM")
    private Instant dcsRecCrtDttm;

    @Column(name = "DCS_LAST_MOD_DTTM")
    private Instant dcsLastModDttm;

    @Column(name = "OBJECTID")
    private Long objectId;

    @Column(name = "search_vector", insertable = false, updatable = false)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.OTHER)
    private Object searchVector;

    public Long getId() {
        return this.id;
    }

    public PassPayment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgencyCode() {
        return this.agencyCode;
    }

    public PassPayment agencyCode(String agencyCode) {
        this.setAgencyCode(agencyCode);
        return this;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getAgencyAcronym() {
        return this.agencyAcronym;
    }

    public PassPayment agencyAcronym(String agencyAcronym) {
        this.setAgencyAcronym(agencyAcronym);
        return this;
    }

    public void setAgencyAcronym(String agencyAcronym) {
        this.agencyAcronym = agencyAcronym;
    }

    public String getAgencyName() {
        return this.agencyName;
    }

    public PassPayment agencyName(String agencyName) {
        this.setAgencyName(agencyName);
        return this;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getContractNumber() {
        return this.contractNumber;
    }

    public PassPayment contractNumber(String contractNumber) {
        this.setContractNumber(contractNumber);
        return this;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getSupplierName() {
        return this.supplierName;
    }

    public PassPayment supplierName(String supplierName) {
        this.setSupplierName(supplierName);
        return this;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public PassPayment invoiceNumber(String invoiceNumber) {
        this.setInvoiceNumber(invoiceNumber);
        return this;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPoNumber() {
        return this.poNumber;
    }

    public PassPayment poNumber(String poNumber) {
        this.setPoNumber(poNumber);
        return this;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getVoucherNumber() {
        return this.voucherNumber;
    }

    public PassPayment voucherNumber(String voucherNumber) {
        this.setVoucherNumber(voucherNumber);
        return this;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public Instant getPaymentDate() {
        return this.paymentDate;
    }

    public PassPayment paymentDate(Instant paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    public PassPayment paymentAmount(BigDecimal paymentAmount) {
        this.setPaymentAmount(paymentAmount);
        return this;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Integer getFiscalYear() {
        return this.fiscalYear;
    }

    public PassPayment fiscalYear(Integer fiscalYear) {
        this.setFiscalYear(fiscalYear);
        return this;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getTransactionCode() {
        return this.transactionCode;
    }

    public PassPayment transactionCode(String transactionCode) {
        this.setTransactionCode(transactionCode);
        return this;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public PassPayment paymentType(String paymentType) {
        this.setPaymentType(paymentType);
        return this;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Instant getInvoiceDate() {
        return this.invoiceDate;
    }

    public PassPayment invoiceDate(Instant invoiceDate) {
        this.setInvoiceDate(invoiceDate);
        return this;
    }

    public void setInvoiceDate(Instant invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Instant getEstPaymentDate() {
        return this.estPaymentDate;
    }

    public PassPayment estPaymentDate(Instant estPaymentDate) {
        this.setEstPaymentDate(estPaymentDate);
        return this;
    }

    public void setEstPaymentDate(Instant estPaymentDate) {
        this.estPaymentDate = estPaymentDate;
    }

    public String getPaymentNumber() {
        return this.paymentNumber;
    }

    public PassPayment paymentNumber(String paymentNumber) {
        this.setPaymentNumber(paymentNumber);
        return this;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public Instant getRecordUpdatedDate() {
        return this.recordUpdatedDate;
    }

    public PassPayment recordUpdatedDate(Instant recordUpdatedDate) {
        this.setRecordUpdatedDate(recordUpdatedDate);
        return this;
    }

    public void setRecordUpdatedDate(Instant recordUpdatedDate) {
        this.recordUpdatedDate = recordUpdatedDate;
    }

    public Instant getRecordCreated() {
        return this.recordCreated;
    }

    public PassPayment recordCreated(Instant recordCreated) {
        this.setRecordCreated(recordCreated);
        return this;
    }

    public void setRecordCreated(Instant recordCreated) {
        this.recordCreated = recordCreated;
    }

    public Instant getDcsRecCrtDttm() {
        return this.dcsRecCrtDttm;
    }

    public PassPayment dcsRecCrtDttm(Instant dcsRecCrtDttm) {
        this.setDcsRecCrtDttm(dcsRecCrtDttm);
        return this;
    }

    public void setDcsRecCrtDttm(Instant dcsRecCrtDttm) {
        this.dcsRecCrtDttm = dcsRecCrtDttm;
    }

    public Instant getDcsLastModDttm() {
        return this.dcsLastModDttm;
    }

    public PassPayment dcsLastModDttm(Instant dcsLastModDttm) {
        this.setDcsLastModDttm(dcsLastModDttm);
        return this;
    }

    public void setDcsLastModDttm(Instant dcsLastModDttm) {
        this.dcsLastModDttm = dcsLastModDttm;
    }

    public Long getObjectId() {
        return this.objectId;
    }

    public PassPayment objectId(Long objectId) {
        this.setObjectId(objectId);
        return this;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PassPayment)) {
            return false;
        }
        return getId() != null && getId().equals(((PassPayment) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PassPayment{" +
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
