package io.argorand.poc.dcpass.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.argorand.poc.dcpass.domain.PassPayment} entity. This class is used
 * in {@link io.argorand.poc.dcpass.web.rest.PassPaymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pass-payments?id.greaterThan=5&poNumber.contains=something&paymentType.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PassPaymentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter agencyCode;
    private StringFilter agencyAcronym;
    private StringFilter agencyName;
    private StringFilter contractNumber;
    private StringFilter supplierName;
    private StringFilter invoiceNumber;
    private StringFilter poNumber;
    private StringFilter voucherNumber;
    private InstantFilter paymentDate;
    private BigDecimalFilter paymentAmount;
    private IntegerFilter fiscalYear;
    private StringFilter transactionCode;
    private StringFilter paymentType;
    private InstantFilter invoiceDate;
    private InstantFilter estPaymentDate;
    private StringFilter paymentNumber;
    private InstantFilter recordUpdatedDate;
    private InstantFilter recordCreated;
    private InstantFilter dcsRecCrtDttm;
    private InstantFilter dcsLastModDttm;
    private LongFilter objectId;
    private String search;
    private Boolean distinct;

    public PassPaymentCriteria() {}

    public PassPaymentCriteria(PassPaymentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.agencyCode = other.optionalAgencyCode().map(StringFilter::copy).orElse(null);
        this.agencyAcronym = other.optionalAgencyAcronym().map(StringFilter::copy).orElse(null);
        this.agencyName = other.optionalAgencyName().map(StringFilter::copy).orElse(null);
        this.contractNumber = other.optionalContractNumber().map(StringFilter::copy).orElse(null);
        this.supplierName = other.optionalSupplierName().map(StringFilter::copy).orElse(null);
        this.invoiceNumber = other.optionalInvoiceNumber().map(StringFilter::copy).orElse(null);
        this.poNumber = other.optionalPoNumber().map(StringFilter::copy).orElse(null);
        this.voucherNumber = other.optionalVoucherNumber().map(StringFilter::copy).orElse(null);
        this.paymentDate = other.optionalPaymentDate().map(InstantFilter::copy).orElse(null);
        this.paymentAmount = other.optionalPaymentAmount().map(BigDecimalFilter::copy).orElse(null);
        this.fiscalYear = other.optionalFiscalYear().map(IntegerFilter::copy).orElse(null);
        this.transactionCode = other.optionalTransactionCode().map(StringFilter::copy).orElse(null);
        this.paymentType = other.optionalPaymentType().map(StringFilter::copy).orElse(null);
        this.invoiceDate = other.optionalInvoiceDate().map(InstantFilter::copy).orElse(null);
        this.estPaymentDate = other.optionalEstPaymentDate().map(InstantFilter::copy).orElse(null);
        this.paymentNumber = other.optionalPaymentNumber().map(StringFilter::copy).orElse(null);
        this.recordUpdatedDate = other.optionalRecordUpdatedDate().map(InstantFilter::copy).orElse(null);
        this.recordCreated = other.optionalRecordCreated().map(InstantFilter::copy).orElse(null);
        this.dcsRecCrtDttm = other.optionalDcsRecCrtDttm().map(InstantFilter::copy).orElse(null);
        this.dcsLastModDttm = other.optionalDcsLastModDttm().map(InstantFilter::copy).orElse(null);
        this.objectId = other.optionalObjectId().map(LongFilter::copy).orElse(null);
        this.search = other.search;
        this.distinct = other.distinct;
    }

    @Override
    public PassPaymentCriteria copy() {
        return new PassPaymentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAgencyCode() {
        return agencyCode;
    }

    public Optional<StringFilter> optionalAgencyCode() {
        return Optional.ofNullable(agencyCode);
    }

    public StringFilter agencyCode() {
        if (agencyCode == null) {
            setAgencyCode(new StringFilter());
        }
        return agencyCode;
    }

    public void setAgencyCode(StringFilter agencyCode) {
        this.agencyCode = agencyCode;
    }

    public StringFilter getAgencyAcronym() {
        return agencyAcronym;
    }

    public Optional<StringFilter> optionalAgencyAcronym() {
        return Optional.ofNullable(agencyAcronym);
    }

    public StringFilter agencyAcronym() {
        if (agencyAcronym == null) {
            setAgencyAcronym(new StringFilter());
        }
        return agencyAcronym;
    }

    public void setAgencyAcronym(StringFilter agencyAcronym) {
        this.agencyAcronym = agencyAcronym;
    }

    public StringFilter getAgencyName() {
        return agencyName;
    }

    public Optional<StringFilter> optionalAgencyName() {
        return Optional.ofNullable(agencyName);
    }

    public StringFilter agencyName() {
        if (agencyName == null) {
            setAgencyName(new StringFilter());
        }
        return agencyName;
    }

    public void setAgencyName(StringFilter agencyName) {
        this.agencyName = agencyName;
    }

    public StringFilter getContractNumber() {
        return contractNumber;
    }

    public Optional<StringFilter> optionalContractNumber() {
        return Optional.ofNullable(contractNumber);
    }

    public StringFilter contractNumber() {
        if (contractNumber == null) {
            setContractNumber(new StringFilter());
        }
        return contractNumber;
    }

    public void setContractNumber(StringFilter contractNumber) {
        this.contractNumber = contractNumber;
    }

    public StringFilter getSupplierName() {
        return supplierName;
    }

    public Optional<StringFilter> optionalSupplierName() {
        return Optional.ofNullable(supplierName);
    }

    public StringFilter supplierName() {
        if (supplierName == null) {
            setSupplierName(new StringFilter());
        }
        return supplierName;
    }

    public void setSupplierName(StringFilter supplierName) {
        this.supplierName = supplierName;
    }

    public StringFilter getInvoiceNumber() {
        return invoiceNumber;
    }

    public Optional<StringFilter> optionalInvoiceNumber() {
        return Optional.ofNullable(invoiceNumber);
    }

    public StringFilter invoiceNumber() {
        if (invoiceNumber == null) {
            setInvoiceNumber(new StringFilter());
        }
        return invoiceNumber;
    }

    public void setInvoiceNumber(StringFilter invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public StringFilter getPoNumber() {
        return poNumber;
    }

    public Optional<StringFilter> optionalPoNumber() {
        return Optional.ofNullable(poNumber);
    }

    public StringFilter poNumber() {
        if (poNumber == null) {
            setPoNumber(new StringFilter());
        }
        return poNumber;
    }

    public void setPoNumber(StringFilter poNumber) {
        this.poNumber = poNumber;
    }

    public StringFilter getVoucherNumber() {
        return voucherNumber;
    }

    public Optional<StringFilter> optionalVoucherNumber() {
        return Optional.ofNullable(voucherNumber);
    }

    public StringFilter voucherNumber() {
        if (voucherNumber == null) {
            setVoucherNumber(new StringFilter());
        }
        return voucherNumber;
    }

    public void setVoucherNumber(StringFilter voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public InstantFilter getPaymentDate() {
        return paymentDate;
    }

    public Optional<InstantFilter> optionalPaymentDate() {
        return Optional.ofNullable(paymentDate);
    }

    public InstantFilter paymentDate() {
        if (paymentDate == null) {
            setPaymentDate(new InstantFilter());
        }
        return paymentDate;
    }

    public void setPaymentDate(InstantFilter paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimalFilter getPaymentAmount() {
        return paymentAmount;
    }

    public Optional<BigDecimalFilter> optionalPaymentAmount() {
        return Optional.ofNullable(paymentAmount);
    }

    public BigDecimalFilter paymentAmount() {
        if (paymentAmount == null) {
            setPaymentAmount(new BigDecimalFilter());
        }
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimalFilter paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public IntegerFilter getFiscalYear() {
        return fiscalYear;
    }

    public Optional<IntegerFilter> optionalFiscalYear() {
        return Optional.ofNullable(fiscalYear);
    }

    public IntegerFilter fiscalYear() {
        if (fiscalYear == null) {
            setFiscalYear(new IntegerFilter());
        }
        return fiscalYear;
    }

    public void setFiscalYear(IntegerFilter fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public StringFilter getTransactionCode() {
        return transactionCode;
    }

    public Optional<StringFilter> optionalTransactionCode() {
        return Optional.ofNullable(transactionCode);
    }

    public StringFilter transactionCode() {
        if (transactionCode == null) {
            setTransactionCode(new StringFilter());
        }
        return transactionCode;
    }

    public void setTransactionCode(StringFilter transactionCode) {
        this.transactionCode = transactionCode;
    }

    public StringFilter getPaymentType() {
        return paymentType;
    }

    public Optional<StringFilter> optionalPaymentType() {
        return Optional.ofNullable(paymentType);
    }

    public StringFilter paymentType() {
        if (paymentType == null) {
            setPaymentType(new StringFilter());
        }
        return paymentType;
    }

    public void setPaymentType(StringFilter paymentType) {
        this.paymentType = paymentType;
    }

    public InstantFilter getInvoiceDate() {
        return invoiceDate;
    }

    public Optional<InstantFilter> optionalInvoiceDate() {
        return Optional.ofNullable(invoiceDate);
    }

    public InstantFilter invoiceDate() {
        if (invoiceDate == null) {
            setInvoiceDate(new InstantFilter());
        }
        return invoiceDate;
    }

    public void setInvoiceDate(InstantFilter invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public InstantFilter getEstPaymentDate() {
        return estPaymentDate;
    }

    public Optional<InstantFilter> optionalEstPaymentDate() {
        return Optional.ofNullable(estPaymentDate);
    }

    public InstantFilter estPaymentDate() {
        if (estPaymentDate == null) {
            setEstPaymentDate(new InstantFilter());
        }
        return estPaymentDate;
    }

    public void setEstPaymentDate(InstantFilter estPaymentDate) {
        this.estPaymentDate = estPaymentDate;
    }

    public StringFilter getPaymentNumber() {
        return paymentNumber;
    }

    public Optional<StringFilter> optionalPaymentNumber() {
        return Optional.ofNullable(paymentNumber);
    }

    public StringFilter paymentNumber() {
        if (paymentNumber == null) {
            setPaymentNumber(new StringFilter());
        }
        return paymentNumber;
    }

    public void setPaymentNumber(StringFilter paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public InstantFilter getRecordUpdatedDate() {
        return recordUpdatedDate;
    }

    public Optional<InstantFilter> optionalRecordUpdatedDate() {
        return Optional.ofNullable(recordUpdatedDate);
    }

    public InstantFilter recordUpdatedDate() {
        if (recordUpdatedDate == null) {
            setRecordUpdatedDate(new InstantFilter());
        }
        return recordUpdatedDate;
    }

    public void setRecordUpdatedDate(InstantFilter recordUpdatedDate) {
        this.recordUpdatedDate = recordUpdatedDate;
    }

    public InstantFilter getRecordCreated() {
        return recordCreated;
    }

    public Optional<InstantFilter> optionalRecordCreated() {
        return Optional.ofNullable(recordCreated);
    }

    public InstantFilter recordCreated() {
        if (recordCreated == null) {
            setRecordCreated(new InstantFilter());
        }
        return recordCreated;
    }

    public void setRecordCreated(InstantFilter recordCreated) {
        this.recordCreated = recordCreated;
    }

    public InstantFilter getDcsRecCrtDttm() {
        return dcsRecCrtDttm;
    }

    public Optional<InstantFilter> optionalDcsRecCrtDttm() {
        return Optional.ofNullable(dcsRecCrtDttm);
    }

    public InstantFilter dcsRecCrtDttm() {
        if (dcsRecCrtDttm == null) {
            setDcsRecCrtDttm(new InstantFilter());
        }
        return dcsRecCrtDttm;
    }

    public void setDcsRecCrtDttm(InstantFilter dcsRecCrtDttm) {
        this.dcsRecCrtDttm = dcsRecCrtDttm;
    }

    public InstantFilter getDcsLastModDttm() {
        return dcsLastModDttm;
    }

    public Optional<InstantFilter> optionalDcsLastModDttm() {
        return Optional.ofNullable(dcsLastModDttm);
    }

    public InstantFilter dcsLastModDttm() {
        if (dcsLastModDttm == null) {
            setDcsLastModDttm(new InstantFilter());
        }
        return dcsLastModDttm;
    }

    public void setDcsLastModDttm(InstantFilter dcsLastModDttm) {
        this.dcsLastModDttm = dcsLastModDttm;
    }

    public LongFilter getObjectId() {
        return objectId;
    }

    public Optional<LongFilter> optionalObjectId() {
        return Optional.ofNullable(objectId);
    }

    public LongFilter objectId() {
        if (objectId == null) {
            setObjectId(new LongFilter());
        }
        return objectId;
    }

    public void setObjectId(LongFilter objectId) {
        this.objectId = objectId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public PassPaymentCriteria search(String search) {
        this.search = search;
        return this;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PassPaymentCriteria that = (PassPaymentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(agencyCode, that.agencyCode) &&
            Objects.equals(agencyAcronym, that.agencyAcronym) &&
            Objects.equals(agencyName, that.agencyName) &&
            Objects.equals(contractNumber, that.contractNumber) &&
            Objects.equals(supplierName, that.supplierName) &&
            Objects.equals(invoiceNumber, that.invoiceNumber) &&
            Objects.equals(poNumber, that.poNumber) &&
            Objects.equals(voucherNumber, that.voucherNumber) &&
            Objects.equals(paymentDate, that.paymentDate) &&
            Objects.equals(paymentAmount, that.paymentAmount) &&
            Objects.equals(fiscalYear, that.fiscalYear) &&
            Objects.equals(transactionCode, that.transactionCode) &&
            Objects.equals(paymentType, that.paymentType) &&
            Objects.equals(invoiceDate, that.invoiceDate) &&
            Objects.equals(estPaymentDate, that.estPaymentDate) &&
            Objects.equals(paymentNumber, that.paymentNumber) &&
            Objects.equals(recordUpdatedDate, that.recordUpdatedDate) &&
            Objects.equals(recordCreated, that.recordCreated) &&
            Objects.equals(dcsRecCrtDttm, that.dcsRecCrtDttm) &&
            Objects.equals(dcsLastModDttm, that.dcsLastModDttm) &&
            Objects.equals(objectId, that.objectId) &&
            Objects.equals(search, that.search) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            agencyCode,
            agencyAcronym,
            agencyName,
            contractNumber,
            supplierName,
            invoiceNumber,
            poNumber,
            voucherNumber,
            paymentDate,
            paymentAmount,
            fiscalYear,
            transactionCode,
            paymentType,
            invoiceDate,
            estPaymentDate,
            paymentNumber,
            recordUpdatedDate,
            recordCreated,
            dcsRecCrtDttm,
            dcsLastModDttm,
            objectId,
            search,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PassPaymentCriteria{" +

            ( id != null ? "id=" + id + ", " : "" ) + ( agencyCode != null ? "agencyCode=" + agencyCode + ", " : "" ) + ( agencyAcronym != null ? "agencyAcronym=" + agencyAcronym + ", " : "" ) + ( agencyName != null ? "agencyName=" + agencyName + ", " : "" ) + ( contractNumber != null ? "contractNumber=" + contractNumber + ", " : "" ) + ( supplierName != null ? "supplierName=" + supplierName + ", " : "" ) + ( invoiceNumber != null ? "invoiceNumber=" + invoiceNumber + ", " : "" ) + ( poNumber != null ? "poNumber=" + poNumber + ", " : "" ) + ( voucherNumber != null ? "voucherNumber=" + voucherNumber + ", " : "" ) + ( paymentDate != null ? "paymentDate=" + paymentDate + ", " : "" ) + ( paymentAmount != null ? "paymentAmount=" + paymentAmount + ", " : "" ) + ( fiscalYear != null ? "fiscalYear=" + fiscalYear + ", " : "" ) + ( transactionCode != null ? "transactionCode=" + transactionCode + ", " : "" ) + ( paymentType != null ? "paymentType=" + paymentType + ", " : "" ) + ( invoiceDate != null ? "invoiceDate=" + invoiceDate + ", " : "" ) + ( estPaymentDate != null ? "estPaymentDate=" + estPaymentDate + ", " : "" ) + ( paymentNumber != null ? "paymentNumber=" + paymentNumber + ", " : "" ) + ( recordUpdatedDate != null ? "recordUpdatedDate=" + recordUpdatedDate + ", " : "" ) + ( recordCreated != null ? "recordCreated=" + recordCreated + ", " : "" ) + ( dcsRecCrtDttm != null ? "dcsRecCrtDttm=" + dcsRecCrtDttm + ", " : "" ) + ( dcsLastModDttm != null ? "dcsLastModDttm=" + dcsLastModDttm + ", " : "" ) + ( objectId != null ? "objectId=" + objectId + ", " : "" ) + ( search != null ? "search=" + search + ", " : "" ) + ( distinct != null ? "distinct=" + distinct : "" ) +

            "}";
    }
}
