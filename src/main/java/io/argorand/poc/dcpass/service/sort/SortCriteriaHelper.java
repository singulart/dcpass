package io.argorand.poc.dcpass.service.sort;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

/**
 * Builds JPA Criteria {@link Order} clauses that keep SQL {@code NULL} and blank strings at the tail
 * regardless of ascending or descending sort direction.
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
        List<Order> orders = new ArrayList<>(2);
        orders.add(cb.asc(emptyOrNullRank(cb, value, javaType)));
        orders.add(ascending ? cb.asc(value) : cb.desc(value));
        return orders;
    }

    private static Expression<Integer> emptyOrNullRank(CriteriaBuilder cb, Expression<?> value, Class<?> javaType) {
        if (String.class.equals(javaType)) {
            return cb.<Integer>selectCase().when(cb.or(cb.isNull(value), cb.equal(value, "")), 1).otherwise(0);
        }
        return cb.<Integer>selectCase().when(cb.isNull(value), 1).otherwise(0);
    }
}
