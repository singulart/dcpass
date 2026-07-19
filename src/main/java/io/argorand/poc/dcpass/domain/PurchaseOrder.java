package io.argorand.poc.dcpass.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PurchaseOrder.
 */
@Entity
@Table(name = "purchase_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "purchase_order_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "PONUMBER")
    private String poNumber;

    @Column(name = "AGENCYCODE")
    private String agencyCode;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REQUESTER")
    private String requester;

    @Column(name = "REQUISTIONNUMBER")
    private String requisitionNumber;

    @Column(name = "COMMODITYCODE")
    private String commodityCode;

    @Column(name = "COMMODITYNAME")
    private String commodityName;

    @Column(name = "CONTRACTNUMBER")
    private String contractNumber;

    @Column(name = "SUPPLIER")
    private String supplier;

    @Column(name = "ORDEREDDATE")
    private Instant orderedDate;

    @Column(name = "CREATEDATE")
    private Instant createDate;

    @Column(name = "POTOTAL", precision = 21, scale = 2)
    private BigDecimal poTotal;

    @Column(name = "FISCALYEAR")
    private Integer fiscalYear;

    @Column(name = "POTITLE")
    private String poTitle;

    @Column(name = "AGENCY_ACRONYM")
    private String agencyAcronym;

    @Column(name = "AGENCY_NAME")
    private String agencyName;

    @Column(name = "DCS_LAST_MOD_DTTM")
    private Instant dcsLastModDttm;

    @Column(name = "DCS_REC_CRT_DTTM")
    private Instant dcsRecCrtDttm;

    @Column(name = "OBJECT_ID")
    private Long objectId;

    @Column(name = "search_vector", insertable = false, updatable = false)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.OTHER)
    private Object searchVector;

    public Long getId() {
        return this.id;
    }

    public PurchaseOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return this.poNumber;
    }

    public PurchaseOrder poNumber(String poNumber) {
        this.setPoNumber(poNumber);
        return this;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getAgencyCode() {
        return this.agencyCode;
    }

    public PurchaseOrder agencyCode(String agencyCode) {
        this.setAgencyCode(agencyCode);
        return this;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getStatus() {
        return this.status;
    }

    public PurchaseOrder status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequester() {
        return this.requester;
    }

    public PurchaseOrder requester(String requester) {
        this.setRequester(requester);
        return this;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRequisitionNumber() {
        return this.requisitionNumber;
    }

    public PurchaseOrder requisitionNumber(String requisitionNumber) {
        this.setRequisitionNumber(requisitionNumber);
        return this;
    }

    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public String getCommodityCode() {
        return this.commodityCode;
    }

    public PurchaseOrder commodityCode(String commodityCode) {
        this.setCommodityCode(commodityCode);
        return this;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getCommodityName() {
        return this.commodityName;
    }

    public PurchaseOrder commodityName(String commodityName) {
        this.setCommodityName(commodityName);
        return this;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getContractNumber() {
        return this.contractNumber;
    }

    public PurchaseOrder contractNumber(String contractNumber) {
        this.setContractNumber(contractNumber);
        return this;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getSupplier() {
        return this.supplier;
    }

    public PurchaseOrder supplier(String supplier) {
        this.setSupplier(supplier);
        return this;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Instant getOrderedDate() {
        return this.orderedDate;
    }

    public PurchaseOrder orderedDate(Instant orderedDate) {
        this.setOrderedDate(orderedDate);
        return this;
    }

    public void setOrderedDate(Instant orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Instant getCreateDate() {
        return this.createDate;
    }

    public PurchaseOrder createDate(Instant createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getPoTotal() {
        return this.poTotal;
    }

    public PurchaseOrder poTotal(BigDecimal poTotal) {
        this.setPoTotal(poTotal);
        return this;
    }

    public void setPoTotal(BigDecimal poTotal) {
        this.poTotal = poTotal;
    }

    public Integer getFiscalYear() {
        return this.fiscalYear;
    }

    public PurchaseOrder fiscalYear(Integer fiscalYear) {
        this.setFiscalYear(fiscalYear);
        return this;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getPoTitle() {
        return this.poTitle;
    }

    public PurchaseOrder poTitle(String poTitle) {
        this.setPoTitle(poTitle);
        return this;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }

    public String getAgencyAcronym() {
        return this.agencyAcronym;
    }

    public PurchaseOrder agencyAcronym(String agencyAcronym) {
        this.setAgencyAcronym(agencyAcronym);
        return this;
    }

    public void setAgencyAcronym(String agencyAcronym) {
        this.agencyAcronym = agencyAcronym;
    }

    public String getAgencyName() {
        return this.agencyName;
    }

    public PurchaseOrder agencyName(String agencyName) {
        this.setAgencyName(agencyName);
        return this;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public Instant getDcsLastModDttm() {
        return this.dcsLastModDttm;
    }

    public PurchaseOrder dcsLastModDttm(Instant dcsLastModDttm) {
        this.setDcsLastModDttm(dcsLastModDttm);
        return this;
    }

    public void setDcsLastModDttm(Instant dcsLastModDttm) {
        this.dcsLastModDttm = dcsLastModDttm;
    }

    public Instant getDcsRecCrtDttm() {
        return this.dcsRecCrtDttm;
    }

    public PurchaseOrder dcsRecCrtDttm(Instant dcsRecCrtDttm) {
        this.setDcsRecCrtDttm(dcsRecCrtDttm);
        return this;
    }

    public void setDcsRecCrtDttm(Instant dcsRecCrtDttm) {
        this.dcsRecCrtDttm = dcsRecCrtDttm;
    }

    public Long getObjectId() {
        return this.objectId;
    }

    public PurchaseOrder objectId(Long objectId) {
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
        if (!(o instanceof PurchaseOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((PurchaseOrder) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrder{" +
            "id=" + getId() + ", poNumber=\'" + getPoNumber() + "\'" + ", agencyCode=\'" + getAgencyCode() + "\'" + ", status=\'" + getStatus() + "\'" + ", requester=\'" + getRequester() + "\'" + ", requisitionNumber=\'" + getRequisitionNumber() + "\'" + ", commodityCode=\'" + getCommodityCode() + "\'" + ", commodityName=\'" + getCommodityName() + "\'" + ", contractNumber=\'" + getContractNumber() + "\'" + ", supplier=\'" + getSupplier() + "\'" + ", orderedDate=" + getOrderedDate() + ", createDate=" + getCreateDate() + ", poTotal=" + getPoTotal() + ", fiscalYear=" + getFiscalYear() + ", poTitle=\'" + getPoTitle() + "\'" + ", agencyAcronym=\'" + getAgencyAcronym() + "\'" + ", agencyName=\'" + getAgencyName() + "\'" + ", dcsLastModDttm=" + getDcsLastModDttm() + ", dcsRecCrtDttm=" + getDcsRecCrtDttm() + ", objectId=" + getObjectId() +
            "}";
    }
}
