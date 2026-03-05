package io.argorand.poc.dcpass.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PassContractCriteriaTest {

    @Test
    void newPassContractCriteriaHasAllFiltersNullTest() {
        var passContractCriteria = new PassContractCriteria();
        assertThat(passContractCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void passContractCriteriaFluentMethodsCreatesFiltersTest() {
        var passContractCriteria = new PassContractCriteria();

        setAllFilters(passContractCriteria);

        assertThat(passContractCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void passContractCriteriaCopyCreatesNullFilterTest() {
        var passContractCriteria = new PassContractCriteria();
        var copy = passContractCriteria.copy();

        assertThat(passContractCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) ->
                        (a == null || a instanceof Boolean) ? a == b : (a instanceof String ? a.equals(b) : (a != b && a.equals(b)))
                    )
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(passContractCriteria)
        );
    }

    @Test
    void passContractCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var passContractCriteria = new PassContractCriteria();
        setAllFilters(passContractCriteria);

        var copy = passContractCriteria.copy();

        assertThat(passContractCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) ->
                        (a == null || a instanceof Boolean) ? a == b : (a instanceof String ? a.equals(b) : (a != b && a.equals(b)))
                    )
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(passContractCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var passContractCriteria = new PassContractCriteria();

        assertThat(passContractCriteria).hasToString("PassContractCriteria{}");
    }

    private static void setAllFilters(PassContractCriteria passContractCriteria) {
        passContractCriteria.id();
        passContractCriteria.procurementMethodDescription();
        passContractCriteria.agencyAcronym();
        passContractCriteria.agencyName();
        passContractCriteria.rowId();
        passContractCriteria.agency();
        passContractCriteria.awardDate();
        passContractCriteria.contractAmount();
        passContractCriteria.endDate();
        passContractCriteria.contractNumber();
        passContractCriteria.startDate();
        passContractCriteria.contractStatus();
        passContractCriteria.title();
        passContractCriteria.contractingOfficer();
        passContractCriteria.fiscalYear();
        passContractCriteria.marketType();
        passContractCriteria.commodityCode();
        passContractCriteria.commodityDescription();
        passContractCriteria.currentOptionPeriod();
        passContractCriteria.totalOptionPeriods();
        passContractCriteria.supplier();
        passContractCriteria.description();
        passContractCriteria.contractTypeDescription();
        passContractCriteria.contractingOfficerEmail();
        passContractCriteria.vendorAddress();
        passContractCriteria.vendorCity();
        passContractCriteria.vendorState();
        passContractCriteria.vendorZip();
        passContractCriteria.publishedVersionId();
        passContractCriteria.documentVersion();
        passContractCriteria.lastModified();
        passContractCriteria.contractingSplst();
        passContractCriteria.contractingSplstEmail();
        passContractCriteria.source();
        passContractCriteria.contractDetailsLink();
        passContractCriteria.contractAdministratorName();
        passContractCriteria.contractAdministratorEmail();
        passContractCriteria.contractAdministratorPhone();
        passContractCriteria.contractOfficerPhone();
        passContractCriteria.cwInternalId();
        passContractCriteria.corporatePhone();
        passContractCriteria.corporateEmailAddress();
        passContractCriteria.recCreatedDate();
        passContractCriteria.recUpdatedDate();
        passContractCriteria.dcsLastModDttm();
        passContractCriteria.objectId();
        passContractCriteria.search("test");
        passContractCriteria.distinct();
    }

    private static Condition<PassContractCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getProcurementMethodDescription()) &&
                condition.apply(criteria.getAgencyAcronym()) &&
                condition.apply(criteria.getAgencyName()) &&
                condition.apply(criteria.getRowId()) &&
                condition.apply(criteria.getAgency()) &&
                condition.apply(criteria.getAwardDate()) &&
                condition.apply(criteria.getContractAmount()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getContractNumber()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getContractStatus()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getContractingOfficer()) &&
                condition.apply(criteria.getFiscalYear()) &&
                condition.apply(criteria.getMarketType()) &&
                condition.apply(criteria.getCommodityCode()) &&
                condition.apply(criteria.getCommodityDescription()) &&
                condition.apply(criteria.getCurrentOptionPeriod()) &&
                condition.apply(criteria.getTotalOptionPeriods()) &&
                condition.apply(criteria.getSupplier()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getContractTypeDescription()) &&
                condition.apply(criteria.getContractingOfficerEmail()) &&
                condition.apply(criteria.getVendorAddress()) &&
                condition.apply(criteria.getVendorCity()) &&
                condition.apply(criteria.getVendorState()) &&
                condition.apply(criteria.getVendorZip()) &&
                condition.apply(criteria.getPublishedVersionId()) &&
                condition.apply(criteria.getDocumentVersion()) &&
                condition.apply(criteria.getLastModified()) &&
                condition.apply(criteria.getContractingSplst()) &&
                condition.apply(criteria.getContractingSplstEmail()) &&
                condition.apply(criteria.getSource()) &&
                condition.apply(criteria.getContractDetailsLink()) &&
                condition.apply(criteria.getContractAdministratorName()) &&
                condition.apply(criteria.getContractAdministratorEmail()) &&
                condition.apply(criteria.getContractAdministratorPhone()) &&
                condition.apply(criteria.getContractOfficerPhone()) &&
                condition.apply(criteria.getCwInternalId()) &&
                condition.apply(criteria.getCorporatePhone()) &&
                condition.apply(criteria.getCorporateEmailAddress()) &&
                condition.apply(criteria.getRecCreatedDate()) &&
                condition.apply(criteria.getRecUpdatedDate()) &&
                condition.apply(criteria.getDcsLastModDttm()) &&
                condition.apply(criteria.getObjectId()) &&
                condition.apply(criteria.getSearch()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PassContractCriteria> copyFiltersAre(
        PassContractCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getProcurementMethodDescription(), copy.getProcurementMethodDescription()) &&
                condition.apply(criteria.getAgencyAcronym(), copy.getAgencyAcronym()) &&
                condition.apply(criteria.getAgencyName(), copy.getAgencyName()) &&
                condition.apply(criteria.getRowId(), copy.getRowId()) &&
                condition.apply(criteria.getAgency(), copy.getAgency()) &&
                condition.apply(criteria.getAwardDate(), copy.getAwardDate()) &&
                condition.apply(criteria.getContractAmount(), copy.getContractAmount()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getContractNumber(), copy.getContractNumber()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getContractStatus(), copy.getContractStatus()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getContractingOfficer(), copy.getContractingOfficer()) &&
                condition.apply(criteria.getFiscalYear(), copy.getFiscalYear()) &&
                condition.apply(criteria.getMarketType(), copy.getMarketType()) &&
                condition.apply(criteria.getCommodityCode(), copy.getCommodityCode()) &&
                condition.apply(criteria.getCommodityDescription(), copy.getCommodityDescription()) &&
                condition.apply(criteria.getCurrentOptionPeriod(), copy.getCurrentOptionPeriod()) &&
                condition.apply(criteria.getTotalOptionPeriods(), copy.getTotalOptionPeriods()) &&
                condition.apply(criteria.getSupplier(), copy.getSupplier()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getContractTypeDescription(), copy.getContractTypeDescription()) &&
                condition.apply(criteria.getContractingOfficerEmail(), copy.getContractingOfficerEmail()) &&
                condition.apply(criteria.getVendorAddress(), copy.getVendorAddress()) &&
                condition.apply(criteria.getVendorCity(), copy.getVendorCity()) &&
                condition.apply(criteria.getVendorState(), copy.getVendorState()) &&
                condition.apply(criteria.getVendorZip(), copy.getVendorZip()) &&
                condition.apply(criteria.getPublishedVersionId(), copy.getPublishedVersionId()) &&
                condition.apply(criteria.getDocumentVersion(), copy.getDocumentVersion()) &&
                condition.apply(criteria.getLastModified(), copy.getLastModified()) &&
                condition.apply(criteria.getContractingSplst(), copy.getContractingSplst()) &&
                condition.apply(criteria.getContractingSplstEmail(), copy.getContractingSplstEmail()) &&
                condition.apply(criteria.getSource(), copy.getSource()) &&
                condition.apply(criteria.getContractDetailsLink(), copy.getContractDetailsLink()) &&
                condition.apply(criteria.getContractAdministratorName(), copy.getContractAdministratorName()) &&
                condition.apply(criteria.getContractAdministratorEmail(), copy.getContractAdministratorEmail()) &&
                condition.apply(criteria.getContractAdministratorPhone(), copy.getContractAdministratorPhone()) &&
                condition.apply(criteria.getContractOfficerPhone(), copy.getContractOfficerPhone()) &&
                condition.apply(criteria.getCwInternalId(), copy.getCwInternalId()) &&
                condition.apply(criteria.getCorporatePhone(), copy.getCorporatePhone()) &&
                condition.apply(criteria.getCorporateEmailAddress(), copy.getCorporateEmailAddress()) &&
                condition.apply(criteria.getRecCreatedDate(), copy.getRecCreatedDate()) &&
                condition.apply(criteria.getRecUpdatedDate(), copy.getRecUpdatedDate()) &&
                condition.apply(criteria.getDcsLastModDttm(), copy.getDcsLastModDttm()) &&
                condition.apply(criteria.getObjectId(), copy.getObjectId()) &&
                condition.apply(criteria.getSearch(), copy.getSearch()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
