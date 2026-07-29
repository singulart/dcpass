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

    @Test
    void hasExplicitColumnSortIsFalseForDefaultIdOrUnsorted() {
        assertThat(SortCriteriaHelper.hasExplicitColumnSort(null)).isFalse();
        assertThat(SortCriteriaHelper.hasExplicitColumnSort(Sort.unsorted())).isFalse();
        assertThat(SortCriteriaHelper.hasExplicitColumnSort(Sort.by(Sort.Direction.ASC, "id"))).isFalse();
        assertThat(SortCriteriaHelper.hasExplicitColumnSort(Sort.by(Sort.Direction.DESC, "id"))).isFalse();
    }

    @Test
    void hasExplicitColumnSortIsTrueForBusinessColumns() {
        assertThat(SortCriteriaHelper.hasExplicitColumnSort(Sort.by(Sort.Direction.ASC, "fiscalYear"))).isTrue();
        assertThat(SortCriteriaHelper.hasExplicitColumnSort(Sort.by(Sort.Direction.DESC, "contractAmount"))).isTrue();
        assertThat(SortCriteriaHelper.hasExplicitColumnSort(Sort.by("id", "fiscalYear"))).isTrue();
    }
}
