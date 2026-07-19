package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.argorand.poc.dcpass.domain.PurchaseOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrderDTO implements Serializable {

    private Long id;
    private String poNumber;
    private String agencyCode;
    private String status;
    private String requester;
    private String requisitionNumber;
    private String commodityCode;
    private String commodityName;
    private String contractNumber;
    private String supplier;
    private Instant orderedDate;
    private Instant createDate;
    private BigDecimal poTotal;
    private Integer fiscalYear;
    private String poTitle;
    private String agencyAcronym;
    private String agencyName;
    private Instant dcsLastModDttm;
    private Instant dcsRecCrtDttm;
    private Long objectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Instant getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Instant orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getPoTotal() {
        return poTotal;
    }

    public void setPoTotal(BigDecimal poTotal) {
        this.poTotal = poTotal;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
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

    public Instant getDcsLastModDttm() {
        return dcsLastModDttm;
    }

    public void setDcsLastModDttm(Instant dcsLastModDttm) {
        this.dcsLastModDttm = dcsLastModDttm;
    }

    public Instant getDcsRecCrtDttm() {
        return dcsRecCrtDttm;
    }

    public void setDcsRecCrtDttm(Instant dcsRecCrtDttm) {
        this.dcsRecCrtDttm = dcsRecCrtDttm;
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
        if (!(o instanceof PurchaseOrderDTO)) {
            return false;
        }

        PurchaseOrderDTO purchaseOrderDTO = (PurchaseOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, purchaseOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrderDTO{" +
            "id=" + getId() + ", poNumber=\'" + getPoNumber() + "\'" + ", agencyCode=\'" + getAgencyCode() + "\'" + ", status=\'" + getStatus() + "\'" + ", requester=\'" + getRequester() + "\'" + ", requisitionNumber=\'" + getRequisitionNumber() + "\'" + ", commodityCode=\'" + getCommodityCode() + "\'" + ", commodityName=\'" + getCommodityName() + "\'" + ", contractNumber=\'" + getContractNumber() + "\'" + ", supplier=\'" + getSupplier() + "\'" + ", orderedDate=" + getOrderedDate() + ", createDate=" + getCreateDate() + ", poTotal=" + getPoTotal() + ", fiscalYear=" + getFiscalYear() + ", poTitle=\'" + getPoTitle() + "\'" + ", agencyAcronym=\'" + getAgencyAcronym() + "\'" + ", agencyName=\'" + getAgencyName() + "\'" + ", dcsLastModDttm=" + getDcsLastModDttm() + ", dcsRecCrtDttm=" + getDcsRecCrtDttm() + ", objectId=" + getObjectId() +
            "}";
    }
}
