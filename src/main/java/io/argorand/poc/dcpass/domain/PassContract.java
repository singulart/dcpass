package io.argorand.poc.dcpass.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PassContract.
 */
@Entity
@Table(name = "pass_contract")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PassContract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "pass_contract_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "PROCUREMENTMETHODDESCRIPTION")
    private String procurementMethodDescription;

    @Column(name = "AGENCY_ACRONYM")
    private String agencyAcronym;

    @Column(name = "AGENCY_NAME")
    private String agencyName;

    @Column(name = "ROW_ID")
    private Long rowId;

    @Column(name = "AGENCY")
    private String agency;

    @Column(name = "AWARDDATE")
    private LocalDate awardDate;

    @Column(name = "CONTRACTAMOUNT", precision = 21, scale = 2)
    private BigDecimal contractAmount;

    @Column(name = "ENDDATE")
    private LocalDate endDate;

    @Column(name = "CONTRACTNUMBER")
    private String contractNumber;

    @Column(name = "STARTDATE")
    private LocalDate startDate;

    @Column(name = "CONTRACTSTATUS")
    private String contractStatus;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTRACTINGOFFICER")
    private String contractingOfficer;

    @Column(name = "FISCALYEAR")
    private Integer fiscalYear;

    @Column(name = "MARKETTYPE")
    private String marketType;

    @Column(name = "COMMODITYCODE")
    private String commodityCode;

    @Column(name = "COMMODITYDESCRIPTION")
    private String commodityDescription;

    @Column(name = "CURRENTOPTIONPERIOD")
    private String currentOptionPeriod;

    @Column(name = "TOTALOPTIONPERIODS")
    private Integer totalOptionPeriods;

    @Column(name = "SUPPLIER")
    private String supplier;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CONTRACTTYPEDESCRIPTION")
    private String contractTypeDescription;

    @Column(name = "CONTRACTINGOFFICEREMAIL")
    private String contractingOfficerEmail;

    @Column(name = "VENDORADDRESS")
    private String vendorAddress;

    @Column(name = "VENDORCITY")
    private String vendorCity;

    @Column(name = "VENDORSTATE")
    private String vendorState;

    @Column(name = "VENDORZIP")
    private String vendorZip;

    @Column(name = "PUBLISHEDVERSIONID")
    private String publishedVersionId;

    @Column(name = "DOCUMENTVERSION")
    private String documentVersion;

    @Column(name = "LASTMODIFIED")
    private Instant lastModified;

    @Column(name = "CONTRACTINGSPLST")
    private String contractingSplst;

    @Column(name = "CONTRACTINGSPLSTEMAIL")
    private String contractingSplstEmail;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "CONTRACT_DETAILS_LINK")
    private String contractDetailsLink;

    @Column(name = "CONTRACTADMINISTRATORNAME")
    private String contractAdministratorName;

    @Column(name = "CONTRACTADMINISTRATOREMAIL")
    private String contractAdministratorEmail;

    @Column(name = "CONTRACTADMINISTRATORPHONE")
    private String contractAdministratorPhone;

    @Column(name = "CONTRACTOFFICERPHONE")
    private String contractOfficerPhone;

    @Column(name = "CWINTERNALID")
    private String cwInternalId;

    @Column(name = "CORPORATEPHONE")
    private String corporatePhone;

    @Column(name = "CORPORATEEMAILADDRESS")
    private String corporateEmailAddress;

    @Column(name = "REC_CREATED_DATE")
    private Instant recCreatedDate;

    @Column(name = "REC_UPDATED_DATE")
    private Instant recUpdatedDate;

    @Column(name = "DCS_LAST_MOD_DTTM")
    private Instant dcsLastModDttm;

    @Column(name = "OBJECTID")
    private Long objectId;

    @Column(name = "search_vector", insertable = false, updatable = false)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.OTHER)
    private Object searchVector;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PassContract id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcurementMethodDescription() {
        return this.procurementMethodDescription;
    }

    public PassContract procurementMethodDescription(String procurementMethodDescription) {
        this.setProcurementMethodDescription(procurementMethodDescription);
        return this;
    }

    public void setProcurementMethodDescription(String procurementMethodDescription) {
        this.procurementMethodDescription = procurementMethodDescription;
    }

    public String getAgencyAcronym() {
        return this.agencyAcronym;
    }

    public PassContract agencyAcronym(String agencyAcronym) {
        this.setAgencyAcronym(agencyAcronym);
        return this;
    }

    public void setAgencyAcronym(String agencyAcronym) {
        this.agencyAcronym = agencyAcronym;
    }

    public String getAgencyName() {
        return this.agencyName;
    }

    public PassContract agencyName(String agencyName) {
        this.setAgencyName(agencyName);
        return this;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public Long getRowId() {
        return this.rowId;
    }

    public PassContract rowId(Long rowId) {
        this.setRowId(rowId);
        return this;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public String getAgency() {
        return this.agency;
    }

    public PassContract agency(String agency) {
        this.setAgency(agency);
        return this;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public LocalDate getAwardDate() {
        return this.awardDate;
    }

    public PassContract awardDate(LocalDate awardDate) {
        this.setAwardDate(awardDate);
        return this;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public BigDecimal getContractAmount() {
        return this.contractAmount;
    }

    public PassContract contractAmount(BigDecimal contractAmount) {
        this.setContractAmount(contractAmount);
        return this;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public PassContract endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getContractNumber() {
        return this.contractNumber;
    }

    public PassContract contractNumber(String contractNumber) {
        this.setContractNumber(contractNumber);
        return this;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public PassContract startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getContractStatus() {
        return this.contractStatus;
    }

    public PassContract contractStatus(String contractStatus) {
        this.setContractStatus(contractStatus);
        return this;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getTitle() {
        return this.title;
    }

    public PassContract title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContractingOfficer() {
        return this.contractingOfficer;
    }

    public PassContract contractingOfficer(String contractingOfficer) {
        this.setContractingOfficer(contractingOfficer);
        return this;
    }

    public void setContractingOfficer(String contractingOfficer) {
        this.contractingOfficer = contractingOfficer;
    }

    public Integer getFiscalYear() {
        return this.fiscalYear;
    }

    public PassContract fiscalYear(Integer fiscalYear) {
        this.setFiscalYear(fiscalYear);
        return this;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getMarketType() {
        return this.marketType;
    }

    public PassContract marketType(String marketType) {
        this.setMarketType(marketType);
        return this;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getCommodityCode() {
        return this.commodityCode;
    }

    public PassContract commodityCode(String commodityCode) {
        this.setCommodityCode(commodityCode);
        return this;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getCommodityDescription() {
        return this.commodityDescription;
    }

    public PassContract commodityDescription(String commodityDescription) {
        this.setCommodityDescription(commodityDescription);
        return this;
    }

    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    public String getCurrentOptionPeriod() {
        return this.currentOptionPeriod;
    }

    public PassContract currentOptionPeriod(String currentOptionPeriod) {
        this.setCurrentOptionPeriod(currentOptionPeriod);
        return this;
    }

    public void setCurrentOptionPeriod(String currentOptionPeriod) {
        this.currentOptionPeriod = currentOptionPeriod;
    }

    public Integer getTotalOptionPeriods() {
        return this.totalOptionPeriods;
    }

    public PassContract totalOptionPeriods(Integer totalOptionPeriods) {
        this.setTotalOptionPeriods(totalOptionPeriods);
        return this;
    }

    public void setTotalOptionPeriods(Integer totalOptionPeriods) {
        this.totalOptionPeriods = totalOptionPeriods;
    }

    public String getSupplier() {
        return this.supplier;
    }

    public PassContract supplier(String supplier) {
        this.setSupplier(supplier);
        return this;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getDescription() {
        return this.description;
    }

    public PassContract description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContractTypeDescription() {
        return this.contractTypeDescription;
    }

    public PassContract contractTypeDescription(String contractTypeDescription) {
        this.setContractTypeDescription(contractTypeDescription);
        return this;
    }

    public void setContractTypeDescription(String contractTypeDescription) {
        this.contractTypeDescription = contractTypeDescription;
    }

    public String getContractingOfficerEmail() {
        return this.contractingOfficerEmail;
    }

    public PassContract contractingOfficerEmail(String contractingOfficerEmail) {
        this.setContractingOfficerEmail(contractingOfficerEmail);
        return this;
    }

    public void setContractingOfficerEmail(String contractingOfficerEmail) {
        this.contractingOfficerEmail = contractingOfficerEmail;
    }

    public String getVendorAddress() {
        return this.vendorAddress;
    }

    public PassContract vendorAddress(String vendorAddress) {
        this.setVendorAddress(vendorAddress);
        return this;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorCity() {
        return this.vendorCity;
    }

    public PassContract vendorCity(String vendorCity) {
        this.setVendorCity(vendorCity);
        return this;
    }

    public void setVendorCity(String vendorCity) {
        this.vendorCity = vendorCity;
    }

    public String getVendorState() {
        return this.vendorState;
    }

    public PassContract vendorState(String vendorState) {
        this.setVendorState(vendorState);
        return this;
    }

    public void setVendorState(String vendorState) {
        this.vendorState = vendorState;
    }

    public String getVendorZip() {
        return this.vendorZip;
    }

    public PassContract vendorZip(String vendorZip) {
        this.setVendorZip(vendorZip);
        return this;
    }

    public void setVendorZip(String vendorZip) {
        this.vendorZip = vendorZip;
    }

    public String getPublishedVersionId() {
        return this.publishedVersionId;
    }

    public PassContract publishedVersionId(String publishedVersionId) {
        this.setPublishedVersionId(publishedVersionId);
        return this;
    }

    public void setPublishedVersionId(String publishedVersionId) {
        this.publishedVersionId = publishedVersionId;
    }

    public String getDocumentVersion() {
        return this.documentVersion;
    }

    public PassContract documentVersion(String documentVersion) {
        this.setDocumentVersion(documentVersion);
        return this;
    }

    public void setDocumentVersion(String documentVersion) {
        this.documentVersion = documentVersion;
    }

    public Instant getLastModified() {
        return this.lastModified;
    }

    public PassContract lastModified(Instant lastModified) {
        this.setLastModified(lastModified);
        return this;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getContractingSplst() {
        return this.contractingSplst;
    }

    public PassContract contractingSplst(String contractingSplst) {
        this.setContractingSplst(contractingSplst);
        return this;
    }

    public void setContractingSplst(String contractingSplst) {
        this.contractingSplst = contractingSplst;
    }

    public String getContractingSplstEmail() {
        return this.contractingSplstEmail;
    }

    public PassContract contractingSplstEmail(String contractingSplstEmail) {
        this.setContractingSplstEmail(contractingSplstEmail);
        return this;
    }

    public void setContractingSplstEmail(String contractingSplstEmail) {
        this.contractingSplstEmail = contractingSplstEmail;
    }

    public String getSource() {
        return this.source;
    }

    public PassContract source(String source) {
        this.setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContractDetailsLink() {
        return this.contractDetailsLink;
    }

    public PassContract contractDetailsLink(String contractDetailsLink) {
        this.setContractDetailsLink(contractDetailsLink);
        return this;
    }

    public void setContractDetailsLink(String contractDetailsLink) {
        this.contractDetailsLink = contractDetailsLink;
    }

    public String getContractAdministratorName() {
        return this.contractAdministratorName;
    }

    public PassContract contractAdministratorName(String contractAdministratorName) {
        this.setContractAdministratorName(contractAdministratorName);
        return this;
    }

    public void setContractAdministratorName(String contractAdministratorName) {
        this.contractAdministratorName = contractAdministratorName;
    }

    public String getContractAdministratorEmail() {
        return this.contractAdministratorEmail;
    }

    public PassContract contractAdministratorEmail(String contractAdministratorEmail) {
        this.setContractAdministratorEmail(contractAdministratorEmail);
        return this;
    }

    public void setContractAdministratorEmail(String contractAdministratorEmail) {
        this.contractAdministratorEmail = contractAdministratorEmail;
    }

    public String getContractAdministratorPhone() {
        return this.contractAdministratorPhone;
    }

    public PassContract contractAdministratorPhone(String contractAdministratorPhone) {
        this.setContractAdministratorPhone(contractAdministratorPhone);
        return this;
    }

    public void setContractAdministratorPhone(String contractAdministratorPhone) {
        this.contractAdministratorPhone = contractAdministratorPhone;
    }

    public String getContractOfficerPhone() {
        return this.contractOfficerPhone;
    }

    public PassContract contractOfficerPhone(String contractOfficerPhone) {
        this.setContractOfficerPhone(contractOfficerPhone);
        return this;
    }

    public void setContractOfficerPhone(String contractOfficerPhone) {
        this.contractOfficerPhone = contractOfficerPhone;
    }

    public String getCwInternalId() {
        return this.cwInternalId;
    }

    public PassContract cwInternalId(String cwInternalId) {
        this.setCwInternalId(cwInternalId);
        return this;
    }

    public void setCwInternalId(String cwInternalId) {
        this.cwInternalId = cwInternalId;
    }

    public String getCorporatePhone() {
        return this.corporatePhone;
    }

    public PassContract corporatePhone(String corporatePhone) {
        this.setCorporatePhone(corporatePhone);
        return this;
    }

    public void setCorporatePhone(String corporatePhone) {
        this.corporatePhone = corporatePhone;
    }

    public String getCorporateEmailAddress() {
        return this.corporateEmailAddress;
    }

    public PassContract corporateEmailAddress(String corporateEmailAddress) {
        this.setCorporateEmailAddress(corporateEmailAddress);
        return this;
    }

    public void setCorporateEmailAddress(String corporateEmailAddress) {
        this.corporateEmailAddress = corporateEmailAddress;
    }

    public Instant getRecCreatedDate() {
        return this.recCreatedDate;
    }

    public PassContract recCreatedDate(Instant recCreatedDate) {
        this.setRecCreatedDate(recCreatedDate);
        return this;
    }

    public void setRecCreatedDate(Instant recCreatedDate) {
        this.recCreatedDate = recCreatedDate;
    }

    public Instant getRecUpdatedDate() {
        return this.recUpdatedDate;
    }

    public PassContract recUpdatedDate(Instant recUpdatedDate) {
        this.setRecUpdatedDate(recUpdatedDate);
        return this;
    }

    public void setRecUpdatedDate(Instant recUpdatedDate) {
        this.recUpdatedDate = recUpdatedDate;
    }

    public Instant getDcsLastModDttm() {
        return this.dcsLastModDttm;
    }

    public PassContract dcsLastModDttm(Instant dcsLastModDttm) {
        this.setDcsLastModDttm(dcsLastModDttm);
        return this;
    }

    public void setDcsLastModDttm(Instant dcsLastModDttm) {
        this.dcsLastModDttm = dcsLastModDttm;
    }

    public Long getObjectId() {
        return this.objectId;
    }

    public PassContract objectId(Long objectId) {
        this.setObjectId(objectId);
        return this;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PassContract)) {
            return false;
        }
        return getId() != null && getId().equals(((PassContract) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PassContract{" +
            "id=" + getId() +
            ", procurementMethodDescription='" + getProcurementMethodDescription() + "'" +
            ", agencyAcronym='" + getAgencyAcronym() + "'" +
            ", agencyName='" + getAgencyName() + "'" +
            ", rowId=" + getRowId() +
            ", agency='" + getAgency() + "'" +
            ", awardDate='" + getAwardDate() + "'" +
            ", contractAmount=" + getContractAmount() +
            ", endDate='" + getEndDate() + "'" +
            ", contractNumber='" + getContractNumber() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", contractStatus='" + getContractStatus() + "'" +
            ", title='" + getTitle() + "'" +
            ", contractingOfficer='" + getContractingOfficer() + "'" +
            ", fiscalYear=" + getFiscalYear() +
            ", marketType='" + getMarketType() + "'" +
            ", commodityCode='" + getCommodityCode() + "'" +
            ", commodityDescription='" + getCommodityDescription() + "'" +
            ", currentOptionPeriod='" + getCurrentOptionPeriod() + "'" +
            ", totalOptionPeriods=" + getTotalOptionPeriods() +
            ", supplier='" + getSupplier() + "'" +
            ", description='" + getDescription() + "'" +
            ", contractTypeDescription='" + getContractTypeDescription() + "'" +
            ", contractingOfficerEmail='" + getContractingOfficerEmail() + "'" +
            ", vendorAddress='" + getVendorAddress() + "'" +
            ", vendorCity='" + getVendorCity() + "'" +
            ", vendorState='" + getVendorState() + "'" +
            ", vendorZip='" + getVendorZip() + "'" +
            ", publishedVersionId='" + getPublishedVersionId() + "'" +
            ", documentVersion='" + getDocumentVersion() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", contractingSplst='" + getContractingSplst() + "'" +
            ", contractingSplstEmail='" + getContractingSplstEmail() + "'" +
            ", source='" + getSource() + "'" +
            ", contractDetailsLink='" + getContractDetailsLink() + "'" +
            ", contractAdministratorName='" + getContractAdministratorName() + "'" +
            ", contractAdministratorEmail='" + getContractAdministratorEmail() + "'" +
            ", contractAdministratorPhone='" + getContractAdministratorPhone() + "'" +
            ", contractOfficerPhone='" + getContractOfficerPhone() + "'" +
            ", cwInternalId='" + getCwInternalId() + "'" +
            ", corporatePhone='" + getCorporatePhone() + "'" +
            ", corporateEmailAddress='" + getCorporateEmailAddress() + "'" +
            ", recCreatedDate='" + getRecCreatedDate() + "'" +
            ", recUpdatedDate='" + getRecUpdatedDate() + "'" +
            ", dcsLastModDttm='" + getDcsLastModDttm() + "'" +
            ", objectId=" + getObjectId() +
            "}";
    }
}
