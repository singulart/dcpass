package io.argorand.poc.dcpass.service.sort;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

/**
 * Builds JPA Criteria {@link Order} clauses that keep SQL {@code NULL} (and blank strings) at the tail
 * regardless of ascending or descending sort direction.
 * <p>
 * Non-string columns use {@code NULLS LAST} so PostgreSQL can still use btree indexes (e.g. {@code ORDER BY id}).
 * A {@code CASE WHEN col IS NULL} sort key forces a full sort and must be avoided for those columns.
 * Strings still use a rank expression so empty {@code ''} sorts with nulls at the end.
 */
public final class SortCriteriaHelper {

    private SortCriteriaHelper() {}

    public static List<Order> toOrders(CriteriaBuilder cb, Root<?> root, Sort sort) {
        List<Order> orders = new ArrayList<>();
        if (sort == null || !sort.isSorted()) {
            return orders;
        }
        for (Sort.Order sortOrder : sort) {
            orders.addAll(toOrders(cb, root.get(sortOrder.getProperty()), sortOrder.isAscending()));
        }
        return orders;
    }

    public static List<Order> toOrders(CriteriaBuilder cb, Path<?> path, boolean ascending) {
        return toOrders(cb, path, path.getJavaType(), ascending);
    }

    public static List<Order> toOrders(CriteriaBuilder cb, Expression<?> value, Class<?> javaType, boolean ascending) {
        if (String.class.equals(javaType)) {
            List<Order> orders = new ArrayList<>(2);
            orders.add(cb.asc(emptyOrBlankRank(cb, value)));
            orders.add(ascending ? cb.asc(value) : cb.desc(value));
            return orders;
        }
        return List.of(ascending ? cb.asc(value, Nulls.LAST) : cb.desc(value, Nulls.LAST));
    }

    private static Expression<Integer> emptyOrBlankRank(CriteriaBuilder cb, Expression<?> value) {
        return cb.<Integer>selectCase().when(cb.or(cb.isNull(value), cb.equal(value, "")), 1).otherwise(0);
    }
}
