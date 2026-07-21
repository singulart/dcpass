package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.PassContract;
import io.argorand.poc.dcpass.domain.PassContract_;
import io.argorand.poc.dcpass.service.sort.SortCriteriaHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Groups matching pass_contract rows by contract number and returns one representative id per group.
 */
public class PassContractRepositoryImpl implements PassContractRepositoryCustom {

    private static final String FTS_RANK_FUNCTION = "pass_contract_fts_rank";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Long> findDistinctRepresentativeIds(Specification<PassContract> spec, Pageable pageable, String ftsQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        long total = countDistinctGroups(cb, spec);
        if (total == 0) {
            return Page.empty(pageable);
        }

        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PassContract> root = query.from(PassContract.class);
        Expression<String> groupKey = contractGroupKey(cb, root);

        query.select(cb.min(root.get(PassContract_.id)));
        applyPredicate(spec, root, query, cb);
        query.groupBy(groupKey);
        query.orderBy(buildGroupOrders(cb, root, pageable.getSort(), ftsQuery));

        TypedQuery<Long> typedQuery = entityManager.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }

        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }

    private long countDistinctGroups(CriteriaBuilder cb, Specification<PassContract> spec) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PassContract> root = countQuery.from(PassContract.class);
        Expression<String> groupKey = contractGroupKey(cb, root);
        countQuery.select(cb.countDistinct(groupKey));
        applyPredicate(spec, root, countQuery, cb);
        Long total = entityManager.createQuery(countQuery).getSingleResult();
        return total == null ? 0L : total;
    }

    private void applyPredicate(Specification<PassContract> spec, Root<PassContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (spec == null) {
            return;
        }
        Predicate predicate = spec.toPredicate(root, query, cb);
        if (predicate != null) {
            query.where(predicate);
        }
    }

    /**
     * Null contract numbers stay unique per row; non-null values share a group key.
     */
    private Expression<String> contractGroupKey(CriteriaBuilder cb, Root<PassContract> root) {
        return cb
            .<String>selectCase()
            .when(
                cb.isNull(root.get(PassContract_.contractNumber)),
                cb.concat(cb.literal("__null:"), root.get(PassContract_.id).as(String.class))
            )
            .otherwise(root.get(PassContract_.contractNumber));
    }

    private List<Order> buildGroupOrders(CriteriaBuilder cb, Root<PassContract> root, Sort sort, String ftsQuery) {
        List<Order> orders = new ArrayList<>();

        if (ftsQuery != null && !ftsQuery.isBlank()) {
            Expression<Float> rank = cb.function(FTS_RANK_FUNCTION, Float.class, root.get("searchVector"), cb.literal(ftsQuery.trim()));
            orders.add(cb.desc(cb.max(rank)));
        }

        if (sort != null && sort.isSorted()) {
            for (Sort.Order sortOrder : sort) {
                if (isFtsRankSort(sortOrder.getProperty())) {
                    continue;
                }
                Path<?> path = root.get(sortOrder.getProperty());
                Expression<?> aggregated = aggregateSortPath(cb, path, sortOrder.isAscending());
                orders.addAll(SortCriteriaHelper.toOrders(cb, aggregated, path.getJavaType(), sortOrder.isAscending()));
            }
        }

        if (orders.isEmpty()) {
            orders.add(cb.desc(cb.min(root.get(PassContract_.id))));
        }

        return orders;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Expression<?> aggregateSortPath(CriteriaBuilder cb, Path<?> path, boolean ascending) {
        Expression comparable = (Expression) path;
        return ascending ? cb.least(comparable) : cb.greatest(comparable);
    }

    private boolean isFtsRankSort(String property) {
        return property != null && property.contains(FTS_RANK_FUNCTION);
    }
}
