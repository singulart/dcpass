package io.argorand.poc.dcpass.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.argorand.poc.dcpass.domain.PassContract} entity. This class is used
 * in {@link io.argorand.poc.dcpass.web.rest.PassContractResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pass-contracts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PassContractCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter procurementMethodDescription;

    private StringFilter agencyAcronym;

    private StringFilter agencyName;

    private LongFilter rowId;

    private StringFilter agency;

    private LocalDateFilter awardDate;

    private BigDecimalFilter contractAmount;

    private LocalDateFilter endDate;

    private StringFilter contractNumber;

    private LocalDateFilter startDate;

    private StringFilter contractStatus;

    private StringFilter title;

    private StringFilter contractingOfficer;

    private IntegerFilter fiscalYear;

    private StringFilter marketType;

    private StringFilter commodityCode;

    private StringFilter commodityDescription;

    private StringFilter currentOptionPeriod;

    private IntegerFilter totalOptionPeriods;

    private StringFilter supplier;

    private StringFilter description;

    private StringFilter contractTypeDescription;

    private StringFilter contractingOfficerEmail;

    private StringFilter vendorAddress;

    private StringFilter vendorCity;

    private StringFilter vendorState;

    private StringFilter vendorZip;

    private StringFilter publishedVersionId;

    private StringFilter documentVersion;

    private InstantFilter lastModified;

    private StringFilter contractingSplst;

    private StringFilter contractingSplstEmail;

    private StringFilter source;

    private StringFilter contractDetailsLink;

    private StringFilter contractAdministratorName;

    private StringFilter contractAdministratorEmail;

    private StringFilter contractAdministratorPhone;

    private StringFilter contractOfficerPhone;

    private StringFilter cwInternalId;

    private StringFilter corporatePhone;

    private StringFilter corporateEmailAddress;

    private InstantFilter recCreatedDate;

    private InstantFilter recUpdatedDate;

    private InstantFilter dcsLastModDttm;

    private LongFilter objectId;

    private Boolean distinct;

    public PassContractCriteria() {}

    public PassContractCriteria(PassContractCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.procurementMethodDescription = other.optionalProcurementMethodDescription().map(StringFilter::copy).orElse(null);
        this.agencyAcronym = other.optionalAgencyAcronym().map(StringFilter::copy).orElse(null);
        this.agencyName = other.optionalAgencyName().map(StringFilter::copy).orElse(null);
        this.rowId = other.optionalRowId().map(LongFilter::copy).orElse(null);
        this.agency = other.optionalAgency().map(StringFilter::copy).orElse(null);
        this.awardDate = other.optionalAwardDate().map(LocalDateFilter::copy).orElse(null);
        this.contractAmount = other.optionalContractAmount().map(BigDecimalFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(LocalDateFilter::copy).orElse(null);
        this.contractNumber = other.optionalContractNumber().map(StringFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(LocalDateFilter::copy).orElse(null);
        this.contractStatus = other.optionalContractStatus().map(StringFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.contractingOfficer = other.optionalContractingOfficer().map(StringFilter::copy).orElse(null);
        this.fiscalYear = other.optionalFiscalYear().map(IntegerFilter::copy).orElse(null);
        this.marketType = other.optionalMarketType().map(StringFilter::copy).orElse(null);
        this.commodityCode = other.optionalCommodityCode().map(StringFilter::copy).orElse(null);
        this.commodityDescription = other.optionalCommodityDescription().map(StringFilter::copy).orElse(null);
        this.currentOptionPeriod = other.optionalCurrentOptionPeriod().map(StringFilter::copy).orElse(null);
        this.totalOptionPeriods = other.optionalTotalOptionPeriods().map(IntegerFilter::copy).orElse(null);
        this.supplier = other.optionalSupplier().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.contractTypeDescription = other.optionalContractTypeDescription().map(StringFilter::copy).orElse(null);
        this.contractingOfficerEmail = other.optionalContractingOfficerEmail().map(StringFilter::copy).orElse(null);
        this.vendorAddress = other.optionalVendorAddress().map(StringFilter::copy).orElse(null);
        this.vendorCity = other.optionalVendorCity().map(StringFilter::copy).orElse(null);
        this.vendorState = other.optionalVendorState().map(StringFilter::copy).orElse(null);
        this.vendorZip = other.optionalVendorZip().map(StringFilter::copy).orElse(null);
        this.publishedVersionId = other.optionalPublishedVersionId().map(StringFilter::copy).orElse(null);
        this.documentVersion = other.optionalDocumentVersion().map(StringFilter::copy).orElse(null);
        this.lastModified = other.optionalLastModified().map(InstantFilter::copy).orElse(null);
        this.contractingSplst = other.optionalContractingSplst().map(StringFilter::copy).orElse(null);
        this.contractingSplstEmail = other.optionalContractingSplstEmail().map(StringFilter::copy).orElse(null);
        this.source = other.optionalSource().map(StringFilter::copy).orElse(null);
        this.contractDetailsLink = other.optionalContractDetailsLink().map(StringFilter::copy).orElse(null);
        this.contractAdministratorName = other.optionalContractAdministratorName().map(StringFilter::copy).orElse(null);
        this.contractAdministratorEmail = other.optionalContractAdministratorEmail().map(StringFilter::copy).orElse(null);
        this.contractAdministratorPhone = other.optionalContractAdministratorPhone().map(StringFilter::copy).orElse(null);
        this.contractOfficerPhone = other.optionalContractOfficerPhone().map(StringFilter::copy).orElse(null);
        this.cwInternalId = other.optionalCwInternalId().map(StringFilter::copy).orElse(null);
        this.corporatePhone = other.optionalCorporatePhone().map(StringFilter::copy).orElse(null);
        this.corporateEmailAddress = other.optionalCorporateEmailAddress().map(StringFilter::copy).orElse(null);
        this.recCreatedDate = other.optionalRecCreatedDate().map(InstantFilter::copy).orElse(null);
        this.recUpdatedDate = other.optionalRecUpdatedDate().map(InstantFilter::copy).orElse(null);
        this.dcsLastModDttm = other.optionalDcsLastModDttm().map(InstantFilter::copy).orElse(null);
        this.objectId = other.optionalObjectId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PassContractCriteria copy() {
        return new PassContractCriteria(this);
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

    public StringFilter getProcurementMethodDescription() {
        return procurementMethodDescription;
    }

    public Optional<StringFilter> optionalProcurementMethodDescription() {
        return Optional.ofNullable(procurementMethodDescription);
    }

    public StringFilter procurementMethodDescription() {
        if (procurementMethodDescription == null) {
            setProcurementMethodDescription(new StringFilter());
        }
        return procurementMethodDescription;
    }

    public void setProcurementMethodDescription(StringFilter procurementMethodDescription) {
        this.procurementMethodDescription = procurementMethodDescription;
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

    public LongFilter getRowId() {
        return rowId;
    }

    public Optional<LongFilter> optionalRowId() {
        return Optional.ofNullable(rowId);
    }

    public LongFilter rowId() {
        if (rowId == null) {
            setRowId(new LongFilter());
        }
        return rowId;
    }

    public void setRowId(LongFilter rowId) {
        this.rowId = rowId;
    }

    public StringFilter getAgency() {
        return agency;
    }

    public Optional<StringFilter> optionalAgency() {
        return Optional.ofNullable(agency);
    }

    public StringFilter agency() {
        if (agency == null) {
            setAgency(new StringFilter());
        }
        return agency;
    }

    public void setAgency(StringFilter agency) {
        this.agency = agency;
    }

    public LocalDateFilter getAwardDate() {
        return awardDate;
    }

    public Optional<LocalDateFilter> optionalAwardDate() {
        return Optional.ofNullable(awardDate);
    }

    public LocalDateFilter awardDate() {
        if (awardDate == null) {
            setAwardDate(new LocalDateFilter());
        }
        return awardDate;
    }

    public void setAwardDate(LocalDateFilter awardDate) {
        this.awardDate = awardDate;
    }

    public BigDecimalFilter getContractAmount() {
        return contractAmount;
    }

    public Optional<BigDecimalFilter> optionalContractAmount() {
        return Optional.ofNullable(contractAmount);
    }

    public BigDecimalFilter contractAmount() {
        if (contractAmount == null) {
            setContractAmount(new BigDecimalFilter());
        }
        return contractAmount;
    }

    public void setContractAmount(BigDecimalFilter contractAmount) {
        this.contractAmount = contractAmount;
    }

    public LocalDateFilter getEndDate() {
        return endDate;
    }

    public Optional<LocalDateFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public LocalDateFilter endDate() {
        if (endDate == null) {
            setEndDate(new LocalDateFilter());
        }
        return endDate;
    }

    public void setEndDate(LocalDateFilter endDate) {
        this.endDate = endDate;
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

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public Optional<LocalDateFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public LocalDateFilter startDate() {
        if (startDate == null) {
            setStartDate(new LocalDateFilter());
        }
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public StringFilter getContractStatus() {
        return contractStatus;
    }

    public Optional<StringFilter> optionalContractStatus() {
        return Optional.ofNullable(contractStatus);
    }

    public StringFilter contractStatus() {
        if (contractStatus == null) {
            setContractStatus(new StringFilter());
        }
        return contractStatus;
    }

    public void setContractStatus(StringFilter contractStatus) {
        this.contractStatus = contractStatus;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getContractingOfficer() {
        return contractingOfficer;
    }

    public Optional<StringFilter> optionalContractingOfficer() {
        return Optional.ofNullable(contractingOfficer);
    }

    public StringFilter contractingOfficer() {
        if (contractingOfficer == null) {
            setContractingOfficer(new StringFilter());
        }
        return contractingOfficer;
    }

    public void setContractingOfficer(StringFilter contractingOfficer) {
        this.contractingOfficer = contractingOfficer;
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

    public StringFilter getMarketType() {
        return marketType;
    }

    public Optional<StringFilter> optionalMarketType() {
        return Optional.ofNullable(marketType);
    }

    public StringFilter marketType() {
        if (marketType == null) {
            setMarketType(new StringFilter());
        }
        return marketType;
    }

    public void setMarketType(StringFilter marketType) {
        this.marketType = marketType;
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

    public StringFilter getCommodityDescription() {
        return commodityDescription;
    }

    public Optional<StringFilter> optionalCommodityDescription() {
        return Optional.ofNullable(commodityDescription);
    }

    public StringFilter commodityDescription() {
        if (commodityDescription == null) {
            setCommodityDescription(new StringFilter());
        }
        return commodityDescription;
    }

    public void setCommodityDescription(StringFilter commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    public StringFilter getCurrentOptionPeriod() {
        return currentOptionPeriod;
    }

    public Optional<StringFilter> optionalCurrentOptionPeriod() {
        return Optional.ofNullable(currentOptionPeriod);
    }

    public StringFilter currentOptionPeriod() {
        if (currentOptionPeriod == null) {
            setCurrentOptionPeriod(new StringFilter());
        }
        return currentOptionPeriod;
    }

    public void setCurrentOptionPeriod(StringFilter currentOptionPeriod) {
        this.currentOptionPeriod = currentOptionPeriod;
    }

    public IntegerFilter getTotalOptionPeriods() {
        return totalOptionPeriods;
    }

    public Optional<IntegerFilter> optionalTotalOptionPeriods() {
        return Optional.ofNullable(totalOptionPeriods);
    }

    public IntegerFilter totalOptionPeriods() {
        if (totalOptionPeriods == null) {
            setTotalOptionPeriods(new IntegerFilter());
        }
        return totalOptionPeriods;
    }

    public void setTotalOptionPeriods(IntegerFilter totalOptionPeriods) {
        this.totalOptionPeriods = totalOptionPeriods;
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

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getContractTypeDescription() {
        return contractTypeDescription;
    }

    public Optional<StringFilter> optionalContractTypeDescription() {
        return Optional.ofNullable(contractTypeDescription);
    }

    public StringFilter contractTypeDescription() {
        if (contractTypeDescription == null) {
            setContractTypeDescription(new StringFilter());
        }
        return contractTypeDescription;
    }

    public void setContractTypeDescription(StringFilter contractTypeDescription) {
        this.contractTypeDescription = contractTypeDescription;
    }

    public StringFilter getContractingOfficerEmail() {
        return contractingOfficerEmail;
    }

    public Optional<StringFilter> optionalContractingOfficerEmail() {
        return Optional.ofNullable(contractingOfficerEmail);
    }

    public StringFilter contractingOfficerEmail() {
        if (contractingOfficerEmail == null) {
            setContractingOfficerEmail(new StringFilter());
        }
        return contractingOfficerEmail;
    }

    public void setContractingOfficerEmail(StringFilter contractingOfficerEmail) {
        this.contractingOfficerEmail = contractingOfficerEmail;
    }

    public StringFilter getVendorAddress() {
        return vendorAddress;
    }

    public Optional<StringFilter> optionalVendorAddress() {
        return Optional.ofNullable(vendorAddress);
    }

    public StringFilter vendorAddress() {
        if (vendorAddress == null) {
            setVendorAddress(new StringFilter());
        }
        return vendorAddress;
    }

    public void setVendorAddress(StringFilter vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public StringFilter getVendorCity() {
        return vendorCity;
    }

    public Optional<StringFilter> optionalVendorCity() {
        return Optional.ofNullable(vendorCity);
    }

    public StringFilter vendorCity() {
        if (vendorCity == null) {
            setVendorCity(new StringFilter());
        }
        return vendorCity;
    }

    public void setVendorCity(StringFilter vendorCity) {
        this.vendorCity = vendorCity;
    }

    public StringFilter getVendorState() {
        return vendorState;
    }

    public Optional<StringFilter> optionalVendorState() {
        return Optional.ofNullable(vendorState);
    }

    public StringFilter vendorState() {
        if (vendorState == null) {
            setVendorState(new StringFilter());
        }
        return vendorState;
    }

    public void setVendorState(StringFilter vendorState) {
        this.vendorState = vendorState;
    }

    public StringFilter getVendorZip() {
        return vendorZip;
    }

    public Optional<StringFilter> optionalVendorZip() {
        return Optional.ofNullable(vendorZip);
    }

    public StringFilter vendorZip() {
        if (vendorZip == null) {
            setVendorZip(new StringFilter());
        }
        return vendorZip;
    }

    public void setVendorZip(StringFilter vendorZip) {
        this.vendorZip = vendorZip;
    }

    public StringFilter getPublishedVersionId() {
        return publishedVersionId;
    }

    public Optional<StringFilter> optionalPublishedVersionId() {
        return Optional.ofNullable(publishedVersionId);
    }

    public StringFilter publishedVersionId() {
        if (publishedVersionId == null) {
            setPublishedVersionId(new StringFilter());
        }
        return publishedVersionId;
    }

    public void setPublishedVersionId(StringFilter publishedVersionId) {
        this.publishedVersionId = publishedVersionId;
    }

    public StringFilter getDocumentVersion() {
        return documentVersion;
    }

    public Optional<StringFilter> optionalDocumentVersion() {
        return Optional.ofNullable(documentVersion);
    }

    public StringFilter documentVersion() {
        if (documentVersion == null) {
            setDocumentVersion(new StringFilter());
        }
        return documentVersion;
    }

    public void setDocumentVersion(StringFilter documentVersion) {
        this.documentVersion = documentVersion;
    }

    public InstantFilter getLastModified() {
        return lastModified;
    }

    public Optional<InstantFilter> optionalLastModified() {
        return Optional.ofNullable(lastModified);
    }

    public InstantFilter lastModified() {
        if (lastModified == null) {
            setLastModified(new InstantFilter());
        }
        return lastModified;
    }

    public void setLastModified(InstantFilter lastModified) {
        this.lastModified = lastModified;
    }

    public StringFilter getContractingSplst() {
        return contractingSplst;
    }

    public Optional<StringFilter> optionalContractingSplst() {
        return Optional.ofNullable(contractingSplst);
    }

    public StringFilter contractingSplst() {
        if (contractingSplst == null) {
            setContractingSplst(new StringFilter());
        }
        return contractingSplst;
    }

    public void setContractingSplst(StringFilter contractingSplst) {
        this.contractingSplst = contractingSplst;
    }

    public StringFilter getContractingSplstEmail() {
        return contractingSplstEmail;
    }

    public Optional<StringFilter> optionalContractingSplstEmail() {
        return Optional.ofNullable(contractingSplstEmail);
    }

    public StringFilter contractingSplstEmail() {
        if (contractingSplstEmail == null) {
            setContractingSplstEmail(new StringFilter());
        }
        return contractingSplstEmail;
    }

    public void setContractingSplstEmail(StringFilter contractingSplstEmail) {
        this.contractingSplstEmail = contractingSplstEmail;
    }

    public StringFilter getSource() {
        return source;
    }

    public Optional<StringFilter> optionalSource() {
        return Optional.ofNullable(source);
    }

    public StringFilter source() {
        if (source == null) {
            setSource(new StringFilter());
        }
        return source;
    }

    public void setSource(StringFilter source) {
        this.source = source;
    }

    public StringFilter getContractDetailsLink() {
        return contractDetailsLink;
    }

    public Optional<StringFilter> optionalContractDetailsLink() {
        return Optional.ofNullable(contractDetailsLink);
    }

    public StringFilter contractDetailsLink() {
        if (contractDetailsLink == null) {
            setContractDetailsLink(new StringFilter());
        }
        return contractDetailsLink;
    }

    public void setContractDetailsLink(StringFilter contractDetailsLink) {
        this.contractDetailsLink = contractDetailsLink;
    }

    public StringFilter getContractAdministratorName() {
        return contractAdministratorName;
    }

    public Optional<StringFilter> optionalContractAdministratorName() {
        return Optional.ofNullable(contractAdministratorName);
    }

    public StringFilter contractAdministratorName() {
        if (contractAdministratorName == null) {
            setContractAdministratorName(new StringFilter());
        }
        return contractAdministratorName;
    }

    public void setContractAdministratorName(StringFilter contractAdministratorName) {
        this.contractAdministratorName = contractAdministratorName;
    }

    public StringFilter getContractAdministratorEmail() {
        return contractAdministratorEmail;
    }

    public Optional<StringFilter> optionalContractAdministratorEmail() {
        return Optional.ofNullable(contractAdministratorEmail);
    }

    public StringFilter contractAdministratorEmail() {
        if (contractAdministratorEmail == null) {
            setContractAdministratorEmail(new StringFilter());
        }
        return contractAdministratorEmail;
    }

    public void setContractAdministratorEmail(StringFilter contractAdministratorEmail) {
        this.contractAdministratorEmail = contractAdministratorEmail;
    }

    public StringFilter getContractAdministratorPhone() {
        return contractAdministratorPhone;
    }

    public Optional<StringFilter> optionalContractAdministratorPhone() {
        return Optional.ofNullable(contractAdministratorPhone);
    }

    public StringFilter contractAdministratorPhone() {
        if (contractAdministratorPhone == null) {
            setContractAdministratorPhone(new StringFilter());
        }
        return contractAdministratorPhone;
    }

    public void setContractAdministratorPhone(StringFilter contractAdministratorPhone) {
        this.contractAdministratorPhone = contractAdministratorPhone;
    }

    public StringFilter getContractOfficerPhone() {
        return contractOfficerPhone;
    }

    public Optional<StringFilter> optionalContractOfficerPhone() {
        return Optional.ofNullable(contractOfficerPhone);
    }

    public StringFilter contractOfficerPhone() {
        if (contractOfficerPhone == null) {
            setContractOfficerPhone(new StringFilter());
        }
        return contractOfficerPhone;
    }

    public void setContractOfficerPhone(StringFilter contractOfficerPhone) {
        this.contractOfficerPhone = contractOfficerPhone;
    }

    public StringFilter getCwInternalId() {
        return cwInternalId;
    }

    public Optional<StringFilter> optionalCwInternalId() {
        return Optional.ofNullable(cwInternalId);
    }

    public StringFilter cwInternalId() {
        if (cwInternalId == null) {
            setCwInternalId(new StringFilter());
        }
        return cwInternalId;
    }

    public void setCwInternalId(StringFilter cwInternalId) {
        this.cwInternalId = cwInternalId;
    }

    public StringFilter getCorporatePhone() {
        return corporatePhone;
    }

    public Optional<StringFilter> optionalCorporatePhone() {
        return Optional.ofNullable(corporatePhone);
    }

    public StringFilter corporatePhone() {
        if (corporatePhone == null) {
            setCorporatePhone(new StringFilter());
        }
        return corporatePhone;
    }

    public void setCorporatePhone(StringFilter corporatePhone) {
        this.corporatePhone = corporatePhone;
    }

    public StringFilter getCorporateEmailAddress() {
        return corporateEmailAddress;
    }

    public Optional<StringFilter> optionalCorporateEmailAddress() {
        return Optional.ofNullable(corporateEmailAddress);
    }

    public StringFilter corporateEmailAddress() {
        if (corporateEmailAddress == null) {
            setCorporateEmailAddress(new StringFilter());
        }
        return corporateEmailAddress;
    }

    public void setCorporateEmailAddress(StringFilter corporateEmailAddress) {
        this.corporateEmailAddress = corporateEmailAddress;
    }

    public InstantFilter getRecCreatedDate() {
        return recCreatedDate;
    }

    public Optional<InstantFilter> optionalRecCreatedDate() {
        return Optional.ofNullable(recCreatedDate);
    }

    public InstantFilter recCreatedDate() {
        if (recCreatedDate == null) {
            setRecCreatedDate(new InstantFilter());
        }
        return recCreatedDate;
    }

    public void setRecCreatedDate(InstantFilter recCreatedDate) {
        this.recCreatedDate = recCreatedDate;
    }

    public InstantFilter getRecUpdatedDate() {
        return recUpdatedDate;
    }

    public Optional<InstantFilter> optionalRecUpdatedDate() {
        return Optional.ofNullable(recUpdatedDate);
    }

    public InstantFilter recUpdatedDate() {
        if (recUpdatedDate == null) {
            setRecUpdatedDate(new InstantFilter());
        }
        return recUpdatedDate;
    }

    public void setRecUpdatedDate(InstantFilter recUpdatedDate) {
        this.recUpdatedDate = recUpdatedDate;
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
        final PassContractCriteria that = (PassContractCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(procurementMethodDescription, that.procurementMethodDescription) &&
            Objects.equals(agencyAcronym, that.agencyAcronym) &&
            Objects.equals(agencyName, that.agencyName) &&
            Objects.equals(rowId, that.rowId) &&
            Objects.equals(agency, that.agency) &&
            Objects.equals(awardDate, that.awardDate) &&
            Objects.equals(contractAmount, that.contractAmount) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(contractNumber, that.contractNumber) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(contractStatus, that.contractStatus) &&
            Objects.equals(title, that.title) &&
            Objects.equals(contractingOfficer, that.contractingOfficer) &&
            Objects.equals(fiscalYear, that.fiscalYear) &&
            Objects.equals(marketType, that.marketType) &&
            Objects.equals(commodityCode, that.commodityCode) &&
            Objects.equals(commodityDescription, that.commodityDescription) &&
            Objects.equals(currentOptionPeriod, that.currentOptionPeriod) &&
            Objects.equals(totalOptionPeriods, that.totalOptionPeriods) &&
            Objects.equals(supplier, that.supplier) &&
            Objects.equals(description, that.description) &&
            Objects.equals(contractTypeDescription, that.contractTypeDescription) &&
            Objects.equals(contractingOfficerEmail, that.contractingOfficerEmail) &&
            Objects.equals(vendorAddress, that.vendorAddress) &&
            Objects.equals(vendorCity, that.vendorCity) &&
            Objects.equals(vendorState, that.vendorState) &&
            Objects.equals(vendorZip, that.vendorZip) &&
            Objects.equals(publishedVersionId, that.publishedVersionId) &&
            Objects.equals(documentVersion, that.documentVersion) &&
            Objects.equals(lastModified, that.lastModified) &&
            Objects.equals(contractingSplst, that.contractingSplst) &&
            Objects.equals(contractingSplstEmail, that.contractingSplstEmail) &&
            Objects.equals(source, that.source) &&
            Objects.equals(contractDetailsLink, that.contractDetailsLink) &&
            Objects.equals(contractAdministratorName, that.contractAdministratorName) &&
            Objects.equals(contractAdministratorEmail, that.contractAdministratorEmail) &&
            Objects.equals(contractAdministratorPhone, that.contractAdministratorPhone) &&
            Objects.equals(contractOfficerPhone, that.contractOfficerPhone) &&
            Objects.equals(cwInternalId, that.cwInternalId) &&
            Objects.equals(corporatePhone, that.corporatePhone) &&
            Objects.equals(corporateEmailAddress, that.corporateEmailAddress) &&
            Objects.equals(recCreatedDate, that.recCreatedDate) &&
            Objects.equals(recUpdatedDate, that.recUpdatedDate) &&
            Objects.equals(dcsLastModDttm, that.dcsLastModDttm) &&
            Objects.equals(objectId, that.objectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            procurementMethodDescription,
            agencyAcronym,
            agencyName,
            rowId,
            agency,
            awardDate,
            contractAmount,
            endDate,
            contractNumber,
            startDate,
            contractStatus,
            title,
            contractingOfficer,
            fiscalYear,
            marketType,
            commodityCode,
            commodityDescription,
            currentOptionPeriod,
            totalOptionPeriods,
            supplier,
            description,
            contractTypeDescription,
            contractingOfficerEmail,
            vendorAddress,
            vendorCity,
            vendorState,
            vendorZip,
            publishedVersionId,
            documentVersion,
            lastModified,
            contractingSplst,
            contractingSplstEmail,
            source,
            contractDetailsLink,
            contractAdministratorName,
            contractAdministratorEmail,
            contractAdministratorPhone,
            contractOfficerPhone,
            cwInternalId,
            corporatePhone,
            corporateEmailAddress,
            recCreatedDate,
            recUpdatedDate,
            dcsLastModDttm,
            objectId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PassContractCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalProcurementMethodDescription().map(f -> "procurementMethodDescription=" + f + ", ").orElse("") +
            optionalAgencyAcronym().map(f -> "agencyAcronym=" + f + ", ").orElse("") +
            optionalAgencyName().map(f -> "agencyName=" + f + ", ").orElse("") +
            optionalRowId().map(f -> "rowId=" + f + ", ").orElse("") +
            optionalAgency().map(f -> "agency=" + f + ", ").orElse("") +
            optionalAwardDate().map(f -> "awardDate=" + f + ", ").orElse("") +
            optionalContractAmount().map(f -> "contractAmount=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalContractNumber().map(f -> "contractNumber=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalContractStatus().map(f -> "contractStatus=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalContractingOfficer().map(f -> "contractingOfficer=" + f + ", ").orElse("") +
            optionalFiscalYear().map(f -> "fiscalYear=" + f + ", ").orElse("") +
            optionalMarketType().map(f -> "marketType=" + f + ", ").orElse("") +
            optionalCommodityCode().map(f -> "commodityCode=" + f + ", ").orElse("") +
            optionalCommodityDescription().map(f -> "commodityDescription=" + f + ", ").orElse("") +
            optionalCurrentOptionPeriod().map(f -> "currentOptionPeriod=" + f + ", ").orElse("") +
            optionalTotalOptionPeriods().map(f -> "totalOptionPeriods=" + f + ", ").orElse("") +
            optionalSupplier().map(f -> "supplier=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalContractTypeDescription().map(f -> "contractTypeDescription=" + f + ", ").orElse("") +
            optionalContractingOfficerEmail().map(f -> "contractingOfficerEmail=" + f + ", ").orElse("") +
            optionalVendorAddress().map(f -> "vendorAddress=" + f + ", ").orElse("") +
            optionalVendorCity().map(f -> "vendorCity=" + f + ", ").orElse("") +
            optionalVendorState().map(f -> "vendorState=" + f + ", ").orElse("") +
            optionalVendorZip().map(f -> "vendorZip=" + f + ", ").orElse("") +
            optionalPublishedVersionId().map(f -> "publishedVersionId=" + f + ", ").orElse("") +
            optionalDocumentVersion().map(f -> "documentVersion=" + f + ", ").orElse("") +
            optionalLastModified().map(f -> "lastModified=" + f + ", ").orElse("") +
            optionalContractingSplst().map(f -> "contractingSplst=" + f + ", ").orElse("") +
            optionalContractingSplstEmail().map(f -> "contractingSplstEmail=" + f + ", ").orElse("") +
            optionalSource().map(f -> "source=" + f + ", ").orElse("") +
            optionalContractDetailsLink().map(f -> "contractDetailsLink=" + f + ", ").orElse("") +
            optionalContractAdministratorName().map(f -> "contractAdministratorName=" + f + ", ").orElse("") +
            optionalContractAdministratorEmail().map(f -> "contractAdministratorEmail=" + f + ", ").orElse("") +
            optionalContractAdministratorPhone().map(f -> "contractAdministratorPhone=" + f + ", ").orElse("") +
            optionalContractOfficerPhone().map(f -> "contractOfficerPhone=" + f + ", ").orElse("") +
            optionalCwInternalId().map(f -> "cwInternalId=" + f + ", ").orElse("") +
            optionalCorporatePhone().map(f -> "corporatePhone=" + f + ", ").orElse("") +
            optionalCorporateEmailAddress().map(f -> "corporateEmailAddress=" + f + ", ").orElse("") +
            optionalRecCreatedDate().map(f -> "recCreatedDate=" + f + ", ").orElse("") +
            optionalRecUpdatedDate().map(f -> "recUpdatedDate=" + f + ", ").orElse("") +
            optionalDcsLastModDttm().map(f -> "dcsLastModDttm=" + f + ", ").orElse("") +
            optionalObjectId().map(f -> "objectId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
