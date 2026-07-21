package io.argorand.poc.dcpass.service.sort;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class SortCriteriaHelperTest {

    @Test
    void toOrdersReturnsEmptyListForUnsortedSort() {
        assertThat(SortCriteriaHelper.toOrders(null, null, Sort.unsorted())).isEmpty();
        assertThat(SortCriteriaHelper.toOrders(null, null, null)).isEmpty();
    }
}
