package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link io.argorand.poc.dcpass.domain.PassContract} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PassContractDTO implements Serializable {

    private Long id;

    private String procurementMethodDescription;

    private String agencyAcronym;

    private String agencyName;

    private Long rowId;

    private String agency;

    private LocalDate awardDate;

    private BigDecimal contractAmount;

    private LocalDate endDate;

    private String contractNumber;

    private LocalDate startDate;

    private String contractStatus;

    private String title;

    private String contractingOfficer;

    private Integer fiscalYear;

    private String marketType;

    private String commodityCode;

    private String commodityDescription;

    private Integer currentOptionPeriod;

    private Integer totalOptionPeriods;

    private String supplier;

    private String description;

    private String contractTypeDescription;

    private String contractingOfficerEmail;

    private String vendorAddress;

    private String vendorCity;

    private String vendorState;

    private String vendorZip;

    private String publishedVersionId;

    private String documentVersion;

    private Instant lastModified;

    private String contractingSplst;

    private String contractingSplstEmail;

    private String source;

    private String contractDetailsLink;

    private String contractAdministratorName;

    private String contractAdministratorEmail;

    private String contractAdministratorPhone;

    private String contractOfficerPhone;

    private String cwInternalId;

    private String corporatePhone;

    private String corporateEmailAddress;

    private Instant recCreatedDate;

    private Instant recUpdatedDate;

    private Instant dcsLastModDttm;

    private Long objectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcurementMethodDescription() {
        return procurementMethodDescription;
    }

    public void setProcurementMethodDescription(String procurementMethodDescription) {
        this.procurementMethodDescription = procurementMethodDescription;
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

    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContractingOfficer() {
        return contractingOfficer;
    }

    public void setContractingOfficer(String contractingOfficer) {
        this.contractingOfficer = contractingOfficer;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getCommodityDescription() {
        return commodityDescription;
    }

    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    public Integer getCurrentOptionPeriod() {
        return currentOptionPeriod;
    }

    public void setCurrentOptionPeriod(Integer currentOptionPeriod) {
        this.currentOptionPeriod = currentOptionPeriod;
    }

    public Integer getTotalOptionPeriods() {
        return totalOptionPeriods;
    }

    public void setTotalOptionPeriods(Integer totalOptionPeriods) {
        this.totalOptionPeriods = totalOptionPeriods;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContractTypeDescription() {
        return contractTypeDescription;
    }

    public void setContractTypeDescription(String contractTypeDescription) {
        this.contractTypeDescription = contractTypeDescription;
    }

    public String getContractingOfficerEmail() {
        return contractingOfficerEmail;
    }

    public void setContractingOfficerEmail(String contractingOfficerEmail) {
        this.contractingOfficerEmail = contractingOfficerEmail;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorCity() {
        return vendorCity;
    }

    public void setVendorCity(String vendorCity) {
        this.vendorCity = vendorCity;
    }

    public String getVendorState() {
        return vendorState;
    }

    public void setVendorState(String vendorState) {
        this.vendorState = vendorState;
    }

    public String getVendorZip() {
        return vendorZip;
    }

    public void setVendorZip(String vendorZip) {
        this.vendorZip = vendorZip;
    }

    public String getPublishedVersionId() {
        return publishedVersionId;
    }

    public void setPublishedVersionId(String publishedVersionId) {
        this.publishedVersionId = publishedVersionId;
    }

    public String getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(String documentVersion) {
        this.documentVersion = documentVersion;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getContractingSplst() {
        return contractingSplst;
    }

    public void setContractingSplst(String contractingSplst) {
        this.contractingSplst = contractingSplst;
    }

    public String getContractingSplstEmail() {
        return contractingSplstEmail;
    }

    public void setContractingSplstEmail(String contractingSplstEmail) {
        this.contractingSplstEmail = contractingSplstEmail;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContractDetailsLink() {
        return contractDetailsLink;
    }

    public void setContractDetailsLink(String contractDetailsLink) {
        this.contractDetailsLink = contractDetailsLink;
    }

    public String getContractAdministratorName() {
        return contractAdministratorName;
    }

    public void setContractAdministratorName(String contractAdministratorName) {
        this.contractAdministratorName = contractAdministratorName;
    }

    public String getContractAdministratorEmail() {
        return contractAdministratorEmail;
    }

    public void setContractAdministratorEmail(String contractAdministratorEmail) {
        this.contractAdministratorEmail = contractAdministratorEmail;
    }

    public String getContractAdministratorPhone() {
        return contractAdministratorPhone;
    }

    public void setContractAdministratorPhone(String contractAdministratorPhone) {
        this.contractAdministratorPhone = contractAdministratorPhone;
    }

    public String getContractOfficerPhone() {
        return contractOfficerPhone;
    }

    public void setContractOfficerPhone(String contractOfficerPhone) {
        this.contractOfficerPhone = contractOfficerPhone;
    }

    public String getCwInternalId() {
        return cwInternalId;
    }

    public void setCwInternalId(String cwInternalId) {
        this.cwInternalId = cwInternalId;
    }

    public String getCorporatePhone() {
        return corporatePhone;
    }

    public void setCorporatePhone(String corporatePhone) {
        this.corporatePhone = corporatePhone;
    }

    public String getCorporateEmailAddress() {
        return corporateEmailAddress;
    }

    public void setCorporateEmailAddress(String corporateEmailAddress) {
        this.corporateEmailAddress = corporateEmailAddress;
    }

    public Instant getRecCreatedDate() {
        return recCreatedDate;
    }

    public void setRecCreatedDate(Instant recCreatedDate) {
        this.recCreatedDate = recCreatedDate;
    }

    public Instant getRecUpdatedDate() {
        return recUpdatedDate;
    }

    public void setRecUpdatedDate(Instant recUpdatedDate) {
        this.recUpdatedDate = recUpdatedDate;
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
        if (!(o instanceof PassContractDTO)) {
            return false;
        }

        PassContractDTO passContractDTO = (PassContractDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, passContractDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PassContractDTO{" +
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
            ", currentOptionPeriod=" + getCurrentOptionPeriod() +
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
