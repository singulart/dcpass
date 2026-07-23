package io.argorand.poc.dcpass.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PassPaymentCriteriaTest {

    @Test
    void newPassPaymentCriteriaHasAllFiltersNullTest() {
        var passPaymentCriteria = new PassPaymentCriteria();
        assertThat(passPaymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void passPaymentCriteriaFluentMethodsCreatesFiltersTest() {
        var passPaymentCriteria = new PassPaymentCriteria();

        setAllFilters(passPaymentCriteria);

        assertThat(passPaymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void passPaymentCriteriaCopyCreatesNullFilterTest() {
        var passPaymentCriteria = new PassPaymentCriteria();
        var copy = passPaymentCriteria.copy();

        assertThat(passPaymentCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(passPaymentCriteria)
        );
    }

    @Test
    void passPaymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var passPaymentCriteria = new PassPaymentCriteria();
        setAllFilters(passPaymentCriteria);

        var copy = passPaymentCriteria.copy();

        assertThat(passPaymentCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(passPaymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var passPaymentCriteria = new PassPaymentCriteria();

        assertThat(passPaymentCriteria).hasToString("PassPaymentCriteria{}");
    }

    private static void setAllFilters(PassPaymentCriteria passPaymentCriteria) {
        passPaymentCriteria.id();
        passPaymentCriteria.agencyCode();
        passPaymentCriteria.agencyAcronym();
        passPaymentCriteria.agencyName();
        passPaymentCriteria.contractNumber();
        passPaymentCriteria.supplierName();
        passPaymentCriteria.invoiceNumber();
        passPaymentCriteria.poNumber();
        passPaymentCriteria.voucherNumber();
        passPaymentCriteria.paymentDate();
        passPaymentCriteria.paymentAmount();
        passPaymentCriteria.fiscalYear();
        passPaymentCriteria.transactionCode();
        passPaymentCriteria.paymentType();
        passPaymentCriteria.invoiceDate();
        passPaymentCriteria.estPaymentDate();
        passPaymentCriteria.paymentNumber();
        passPaymentCriteria.recordUpdatedDate();
        passPaymentCriteria.recordCreated();
        passPaymentCriteria.dcsRecCrtDttm();
        passPaymentCriteria.dcsLastModDttm();
        passPaymentCriteria.objectId();
        passPaymentCriteria.search("test");
        passPaymentCriteria.distinct();
    }

    private static Condition<PassPaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAgencyCode()) &&
                condition.apply(criteria.getAgencyAcronym()) &&
                condition.apply(criteria.getAgencyName()) &&
                condition.apply(criteria.getContractNumber()) &&
                condition.apply(criteria.getSupplierName()) &&
                condition.apply(criteria.getInvoiceNumber()) &&
                condition.apply(criteria.getPoNumber()) &&
                condition.apply(criteria.getVoucherNumber()) &&
                condition.apply(criteria.getPaymentDate()) &&
                condition.apply(criteria.getPaymentAmount()) &&
                condition.apply(criteria.getFiscalYear()) &&
                condition.apply(criteria.getTransactionCode()) &&
                condition.apply(criteria.getPaymentType()) &&
                condition.apply(criteria.getInvoiceDate()) &&
                condition.apply(criteria.getEstPaymentDate()) &&
                condition.apply(criteria.getPaymentNumber()) &&
                condition.apply(criteria.getRecordUpdatedDate()) &&
                condition.apply(criteria.getRecordCreated()) &&
                condition.apply(criteria.getDcsRecCrtDttm()) &&
                condition.apply(criteria.getDcsLastModDttm()) &&
                condition.apply(criteria.getObjectId()) &&
                condition.apply(criteria.getSearch()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PassPaymentCriteria> copyFiltersAre(PassPaymentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAgencyCode(), copy.getAgencyCode()) &&
                condition.apply(criteria.getAgencyAcronym(), copy.getAgencyAcronym()) &&
                condition.apply(criteria.getAgencyName(), copy.getAgencyName()) &&
                condition.apply(criteria.getContractNumber(), copy.getContractNumber()) &&
                condition.apply(criteria.getSupplierName(), copy.getSupplierName()) &&
                condition.apply(criteria.getInvoiceNumber(), copy.getInvoiceNumber()) &&
                condition.apply(criteria.getPoNumber(), copy.getPoNumber()) &&
                condition.apply(criteria.getVoucherNumber(), copy.getVoucherNumber()) &&
                condition.apply(criteria.getPaymentDate(), copy.getPaymentDate()) &&
                condition.apply(criteria.getPaymentAmount(), copy.getPaymentAmount()) &&
                condition.apply(criteria.getFiscalYear(), copy.getFiscalYear()) &&
                condition.apply(criteria.getTransactionCode(), copy.getTransactionCode()) &&
                condition.apply(criteria.getPaymentType(), copy.getPaymentType()) &&
                condition.apply(criteria.getInvoiceDate(), copy.getInvoiceDate()) &&
                condition.apply(criteria.getEstPaymentDate(), copy.getEstPaymentDate()) &&
                condition.apply(criteria.getPaymentNumber(), copy.getPaymentNumber()) &&
                condition.apply(criteria.getRecordUpdatedDate(), copy.getRecordUpdatedDate()) &&
                condition.apply(criteria.getRecordCreated(), copy.getRecordCreated()) &&
                condition.apply(criteria.getDcsRecCrtDttm(), copy.getDcsRecCrtDttm()) &&
                condition.apply(criteria.getDcsLastModDttm(), copy.getDcsLastModDttm()) &&
                condition.apply(criteria.getObjectId(), copy.getObjectId()) &&
                condition.apply(criteria.getSearch(), copy.getSearch()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
