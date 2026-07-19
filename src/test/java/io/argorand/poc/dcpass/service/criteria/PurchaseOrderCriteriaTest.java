package io.argorand.poc.dcpass.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PurchaseOrderCriteriaTest {

    @Test
    void newPurchaseOrderCriteriaHasAllFiltersNullTest() {
        var purchaseOrderCriteria = new PurchaseOrderCriteria();
        assertThat(purchaseOrderCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void purchaseOrderCriteriaFluentMethodsCreatesFiltersTest() {
        var purchaseOrderCriteria = new PurchaseOrderCriteria();

        setAllFilters(purchaseOrderCriteria);

        assertThat(purchaseOrderCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void purchaseOrderCriteriaCopyCreatesNullFilterTest() {
        var purchaseOrderCriteria = new PurchaseOrderCriteria();
        var copy = purchaseOrderCriteria.copy();

        assertThat(purchaseOrderCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(purchaseOrderCriteria)
        );
    }

    @Test
    void purchaseOrderCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var purchaseOrderCriteria = new PurchaseOrderCriteria();
        setAllFilters(purchaseOrderCriteria);

        var copy = purchaseOrderCriteria.copy();

        assertThat(purchaseOrderCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(purchaseOrderCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var purchaseOrderCriteria = new PurchaseOrderCriteria();

        assertThat(purchaseOrderCriteria).hasToString("PurchaseOrderCriteria{}");
    }

    private static void setAllFilters(PurchaseOrderCriteria purchaseOrderCriteria) {
        purchaseOrderCriteria.id();
        purchaseOrderCriteria.poNumber();
        purchaseOrderCriteria.agencyCode();
        purchaseOrderCriteria.status();
        purchaseOrderCriteria.requester();
        purchaseOrderCriteria.requisitionNumber();
        purchaseOrderCriteria.commodityCode();
        purchaseOrderCriteria.commodityName();
        purchaseOrderCriteria.contractNumber();
        purchaseOrderCriteria.supplier();
        purchaseOrderCriteria.orderedDate();
        purchaseOrderCriteria.createDate();
        purchaseOrderCriteria.poTotal();
        purchaseOrderCriteria.fiscalYear();
        purchaseOrderCriteria.poTitle();
        purchaseOrderCriteria.agencyAcronym();
        purchaseOrderCriteria.agencyName();
        purchaseOrderCriteria.dcsLastModDttm();
        purchaseOrderCriteria.dcsRecCrtDttm();
        purchaseOrderCriteria.objectId();
        purchaseOrderCriteria.search("test");
        purchaseOrderCriteria.distinct();
    }

    private static Condition<PurchaseOrderCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPoNumber()) &&
                condition.apply(criteria.getAgencyCode()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRequester()) &&
                condition.apply(criteria.getRequisitionNumber()) &&
                condition.apply(criteria.getCommodityCode()) &&
                condition.apply(criteria.getCommodityName()) &&
                condition.apply(criteria.getContractNumber()) &&
                condition.apply(criteria.getSupplier()) &&
                condition.apply(criteria.getOrderedDate()) &&
                condition.apply(criteria.getCreateDate()) &&
                condition.apply(criteria.getPoTotal()) &&
                condition.apply(criteria.getFiscalYear()) &&
                condition.apply(criteria.getPoTitle()) &&
                condition.apply(criteria.getAgencyAcronym()) &&
                condition.apply(criteria.getAgencyName()) &&
                condition.apply(criteria.getDcsLastModDttm()) &&
                condition.apply(criteria.getDcsRecCrtDttm()) &&
                condition.apply(criteria.getObjectId()) &&
                condition.apply(criteria.getSearch()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PurchaseOrderCriteria> copyFiltersAre(
        PurchaseOrderCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPoNumber(), copy.getPoNumber()) &&
                condition.apply(criteria.getAgencyCode(), copy.getAgencyCode()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRequester(), copy.getRequester()) &&
                condition.apply(criteria.getRequisitionNumber(), copy.getRequisitionNumber()) &&
                condition.apply(criteria.getCommodityCode(), copy.getCommodityCode()) &&
                condition.apply(criteria.getCommodityName(), copy.getCommodityName()) &&
                condition.apply(criteria.getContractNumber(), copy.getContractNumber()) &&
                condition.apply(criteria.getSupplier(), copy.getSupplier()) &&
                condition.apply(criteria.getOrderedDate(), copy.getOrderedDate()) &&
                condition.apply(criteria.getCreateDate(), copy.getCreateDate()) &&
                condition.apply(criteria.getPoTotal(), copy.getPoTotal()) &&
                condition.apply(criteria.getFiscalYear(), copy.getFiscalYear()) &&
                condition.apply(criteria.getPoTitle(), copy.getPoTitle()) &&
                condition.apply(criteria.getAgencyAcronym(), copy.getAgencyAcronym()) &&
                condition.apply(criteria.getAgencyName(), copy.getAgencyName()) &&
                condition.apply(criteria.getDcsLastModDttm(), copy.getDcsLastModDttm()) &&
                condition.apply(criteria.getDcsRecCrtDttm(), copy.getDcsRecCrtDttm()) &&
                condition.apply(criteria.getObjectId(), copy.getObjectId()) &&
                condition.apply(criteria.getSearch(), copy.getSearch()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
