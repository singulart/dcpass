package io.argorand.poc.dcpass.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.argorand.poc.dcpass.domain.PurchaseOrder} entity. This class is used
 * in {@link io.argorand.poc.dcpass.web.rest.PurchaseOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /purchase-orders?id.greaterThan=5&poNumber.contains=something&status.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrderCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter poNumber;
    private StringFilter agencyCode;
    private StringFilter status;
    private StringFilter requester;
    private StringFilter requisitionNumber;
    private StringFilter commodityCode;
    private StringFilter commodityName;
    private StringFilter contractNumber;
    private StringFilter supplier;
    private InstantFilter orderedDate;
    private InstantFilter createDate;
    private BigDecimalFilter poTotal;
    private IntegerFilter fiscalYear;
    private StringFilter poTitle;
    private StringFilter agencyAcronym;
    private StringFilter agencyName;
    private InstantFilter dcsLastModDttm;
    private InstantFilter dcsRecCrtDttm;
    private LongFilter objectId;
    private String search;
    private Boolean distinct;

    public PurchaseOrderCriteria() {}

    public PurchaseOrderCriteria(PurchaseOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.poNumber = other.optionalPoNumber().map(StringFilter::copy).orElse(null);
        this.agencyCode = other.optionalAgencyCode().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.requester = other.optionalRequester().map(StringFilter::copy).orElse(null);
        this.requisitionNumber = other.optionalRequisitionNumber().map(StringFilter::copy).orElse(null);
        this.commodityCode = other.optionalCommodityCode().map(StringFilter::copy).orElse(null);
        this.commodityName = other.optionalCommodityName().map(StringFilter::copy).orElse(null);
        this.contractNumber = other.optionalContractNumber().map(StringFilter::copy).orElse(null);
        this.supplier = other.optionalSupplier().map(StringFilter::copy).orElse(null);
        this.orderedDate = other.optionalOrderedDate().map(InstantFilter::copy).orElse(null);
        this.createDate = other.optionalCreateDate().map(InstantFilter::copy).orElse(null);
        this.poTotal = other.optionalPoTotal().map(BigDecimalFilter::copy).orElse(null);
        this.fiscalYear = other.optionalFiscalYear().map(IntegerFilter::copy).orElse(null);
        this.poTitle = other.optionalPoTitle().map(StringFilter::copy).orElse(null);
        this.agencyAcronym = other.optionalAgencyAcronym().map(StringFilter::copy).orElse(null);
        this.agencyName = other.optionalAgencyName().map(StringFilter::copy).orElse(null);
        this.dcsLastModDttm = other.optionalDcsLastModDttm().map(InstantFilter::copy).orElse(null);
        this.dcsRecCrtDttm = other.optionalDcsRecCrtDttm().map(InstantFilter::copy).orElse(null);
        this.objectId = other.optionalObjectId().map(LongFilter::copy).orElse(null);
        this.search = other.search;
        this.distinct = other.distinct;
    }

    @Override
    public PurchaseOrderCriteria copy() {
        return new PurchaseOrderCriteria(this);
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

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public StringFilter getRequester() {
        return requester;
    }

    public Optional<StringFilter> optionalRequester() {
        return Optional.ofNullable(requester);
    }

    public StringFilter requester() {
        if (requester == null) {
            setRequester(new StringFilter());
        }
        return requester;
    }

    public void setRequester(StringFilter requester) {
        this.requester = requester;
    }

    public StringFilter getRequisitionNumber() {
        return requisitionNumber;
    }

    public Optional<StringFilter> optionalRequisitionNumber() {
        return Optional.ofNullable(requisitionNumber);
    }

    public StringFilter requisitionNumber() {
        if (requisitionNumber == null) {
            setRequisitionNumber(new StringFilter());
        }
        return requisitionNumber;
    }

    public void setRequisitionNumber(StringFilter requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public StringFilter getCommodityCode() {
        return commodityCode;
    }

    public Optional<StringFilter> optionalCommodityCode() {
        return Optional.ofNullable(commodityCode);
    }

    public StringFilter commodityCode() {
        if (commodityCode == null) {
            setCommodityCode(new StringFilter());
        }
        return commodityCode;
    }

    public void setCommodityCode(StringFilter commodityCode) {
        this.commodityCode = commodityCode;
    }

    public StringFilter getCommodityName() {
        return commodityName;
    }

    public Optional<StringFilter> optionalCommodityName() {
        return Optional.ofNullable(commodityName);
    }

    public StringFilter commodityName() {
        if (commodityName == null) {
            setCommodityName(new StringFilter());
        }
        return commodityName;
    }

    public void setCommodityName(StringFilter commodityName) {
        this.commodityName = commodityName;
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

    public StringFilter getSupplier() {
        return supplier;
    }

    public Optional<StringFilter> optionalSupplier() {
        return Optional.ofNullable(supplier);
    }

    public StringFilter supplier() {
        if (supplier == null) {
            setSupplier(new StringFilter());
        }
        return supplier;
    }

    public void setSupplier(StringFilter supplier) {
        this.supplier = supplier;
    }

    public InstantFilter getOrderedDate() {
        return orderedDate;
    }

    public Optional<InstantFilter> optionalOrderedDate() {
        return Optional.ofNullable(orderedDate);
    }

    public InstantFilter orderedDate() {
        if (orderedDate == null) {
            setOrderedDate(new InstantFilter());
        }
        return orderedDate;
    }

    public void setOrderedDate(InstantFilter orderedDate) {
        this.orderedDate = orderedDate;
    }

    public InstantFilter getCreateDate() {
        return createDate;
    }

    public Optional<InstantFilter> optionalCreateDate() {
        return Optional.ofNullable(createDate);
    }

    public InstantFilter createDate() {
        if (createDate == null) {
            setCreateDate(new InstantFilter());
        }
        return createDate;
    }

    public void setCreateDate(InstantFilter createDate) {
        this.createDate = createDate;
    }

    public BigDecimalFilter getPoTotal() {
        return poTotal;
    }

    public Optional<BigDecimalFilter> optionalPoTotal() {
        return Optional.ofNullable(poTotal);
    }

    public BigDecimalFilter poTotal() {
        if (poTotal == null) {
            setPoTotal(new BigDecimalFilter());
        }
        return poTotal;
    }

    public void setPoTotal(BigDecimalFilter poTotal) {
        this.poTotal = poTotal;
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

    public StringFilter getPoTitle() {
        return poTitle;
    }

    public Optional<StringFilter> optionalPoTitle() {
        return Optional.ofNullable(poTitle);
    }

    public StringFilter poTitle() {
        if (poTitle == null) {
            setPoTitle(new StringFilter());
        }
        return poTitle;
    }

    public void setPoTitle(StringFilter poTitle) {
        this.poTitle = poTitle;
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

    public PurchaseOrderCriteria search(String search) {
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
        final PurchaseOrderCriteria that = (PurchaseOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(poNumber, that.poNumber) &&
            Objects.equals(agencyCode, that.agencyCode) &&
            Objects.equals(status, that.status) &&
            Objects.equals(requester, that.requester) &&
            Objects.equals(requisitionNumber, that.requisitionNumber) &&
            Objects.equals(commodityCode, that.commodityCode) &&
            Objects.equals(commodityName, that.commodityName) &&
            Objects.equals(contractNumber, that.contractNumber) &&
            Objects.equals(supplier, that.supplier) &&
            Objects.equals(orderedDate, that.orderedDate) &&
            Objects.equals(createDate, that.createDate) &&
            Objects.equals(poTotal, that.poTotal) &&
            Objects.equals(fiscalYear, that.fiscalYear) &&
            Objects.equals(poTitle, that.poTitle) &&
            Objects.equals(agencyAcronym, that.agencyAcronym) &&
            Objects.equals(agencyName, that.agencyName) &&
            Objects.equals(dcsLastModDttm, that.dcsLastModDttm) &&
            Objects.equals(dcsRecCrtDttm, that.dcsRecCrtDttm) &&
            Objects.equals(objectId, that.objectId) &&
            Objects.equals(search, that.search) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            poNumber,
            agencyCode,
            status,
            requester,
            requisitionNumber,
            commodityCode,
            commodityName,
            contractNumber,
            supplier,
            orderedDate,
            createDate,
            poTotal,
            fiscalYear,
            poTitle,
            agencyAcronym,
            agencyName,
            dcsLastModDttm,
            dcsRecCrtDttm,
            objectId,
            search,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrderCriteria{" +
            ( id != null ? "id=" + id + ", " : "" ) + ( poNumber != null ? "poNumber=" + poNumber + ", " : "" ) + ( agencyCode != null ? "agencyCode=" + agencyCode + ", " : "" ) + ( status != null ? "status=" + status + ", " : "" ) + ( requester != null ? "requester=" + requester + ", " : "" ) + ( requisitionNumber != null ? "requisitionNumber=" + requisitionNumber + ", " : "" ) + ( commodityCode != null ? "commodityCode=" + commodityCode + ", " : "" ) + ( commodityName != null ? "commodityName=" + commodityName + ", " : "" ) + ( contractNumber != null ? "contractNumber=" + contractNumber + ", " : "" ) + ( supplier != null ? "supplier=" + supplier + ", " : "" ) + ( orderedDate != null ? "orderedDate=" + orderedDate + ", " : "" ) + ( createDate != null ? "createDate=" + createDate + ", " : "" ) + ( poTotal != null ? "poTotal=" + poTotal + ", " : "" ) + ( fiscalYear != null ? "fiscalYear=" + fiscalYear + ", " : "" ) + ( poTitle != null ? "poTitle=" + poTitle + ", " : "" ) + ( agencyAcronym != null ? "agencyAcronym=" + agencyAcronym + ", " : "" ) + ( agencyName != null ? "agencyName=" + agencyName + ", " : "" ) + ( dcsLastModDttm != null ? "dcsLastModDttm=" + dcsLastModDttm + ", " : "" ) + ( dcsRecCrtDttm != null ? "dcsRecCrtDttm=" + dcsRecCrtDttm + ", " : "" ) + ( objectId != null ? "objectId=" + objectId + ", " : "" ) + ( search != null ? "search=" + search + ", " : "" ) + ( distinct != null ? "distinct=" + distinct : "" ) +
            "}";
    }
}
