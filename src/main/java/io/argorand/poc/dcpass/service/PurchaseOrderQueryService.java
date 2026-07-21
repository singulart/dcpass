package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.domain.*; // for static metamodels
import io.argorand.poc.dcpass.domain.PurchaseOrder;
import io.argorand.poc.dcpass.repository.PurchaseOrderRepository;
import io.argorand.poc.dcpass.service.criteria.PurchaseOrderCriteria;
import io.argorand.poc.dcpass.service.dto.PurchaseOrderDTO;
import io.argorand.poc.dcpass.service.mapper.PurchaseOrderMapper;
import io.argorand.poc.dcpass.service.sort.SortCriteriaHelper;
import jakarta.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PurchaseOrder} entities in the database.
 * The main input is a {@link PurchaseOrderCriteria} which gets converted to {@link Specification}.
 */
@Service
@Transactional(readOnly = true)
public class PurchaseOrderQueryService extends QueryService<PurchaseOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderQueryService.class);

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderQueryService(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderMapper purchaseOrderMapper) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    /**
     * Return a {@link Page} of {@link PurchaseOrderDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> findByCriteria(PurchaseOrderCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PurchaseOrder> specification = createSpecification(criteria);
        String ftsQuery = criteria != null ? criteria.getSearch() : null;
        Sort userSort = page.getSort();
        boolean hasFts = ftsQuery != null && !ftsQuery.isBlank();
        boolean hasUserSort = userSort.isSorted();

        Pageable pageForQuery = hasFts || hasUserSort ? PageRequest.of(page.getPageNumber(), page.getPageSize()) : page;
        Specification<PurchaseOrder> querySpec = specification;
        if (hasFts || hasUserSort) {
            String trimmedFts = hasFts ? ftsQuery.trim() : null;
            querySpec = specification.and((root, query, cb) -> {
                if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                    List<Order> orders = new ArrayList<>();
                    if (hasFts) {
                        orders.add(
                            cb.desc(cb.function("purchase_order_fts_rank", Float.class, root.get("searchVector"), cb.literal(trimmedFts)))
                        );
                    }
                    if (hasUserSort) {
                        orders.addAll(SortCriteriaHelper.toOrders(cb, root, userSort));
                    }
                    if (!orders.isEmpty()) {
                        query.orderBy(orders);
                    }
                }
                return cb.conjunction();
            });
        }
        return purchaseOrderRepository.findAll(querySpec, pageForQuery).map(purchaseOrderMapper::toDto);
    }

    /**
     * Function to convert {@link PurchaseOrderCriteria} to a {@link Specification}.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PurchaseOrder> createSpecification(PurchaseOrderCriteria criteria) {
        Specification<PurchaseOrder> specification = Specification.unrestricted();
        if (criteria != null) {
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), PurchaseOrder_.id),
                buildStringSpecification(criteria.getPoNumber(), PurchaseOrder_.poNumber),
                buildStringSpecification(criteria.getAgencyCode(), PurchaseOrder_.agencyCode),
                buildStringSpecification(criteria.getStatus(), PurchaseOrder_.status),
                buildStringSpecification(criteria.getRequester(), PurchaseOrder_.requester),
                buildStringSpecification(criteria.getRequisitionNumber(), PurchaseOrder_.requisitionNumber),
                buildStringSpecification(criteria.getCommodityCode(), PurchaseOrder_.commodityCode),
                buildStringSpecification(criteria.getCommodityName(), PurchaseOrder_.commodityName),
                buildStringSpecification(criteria.getContractNumber(), PurchaseOrder_.contractNumber),
                buildStringSpecification(criteria.getSupplier(), PurchaseOrder_.supplier),
                buildRangeSpecification(criteria.getOrderedDate(), PurchaseOrder_.orderedDate),
                buildRangeSpecification(criteria.getCreateDate(), PurchaseOrder_.createDate),
                buildRangeSpecification(criteria.getPoTotal(), PurchaseOrder_.poTotal),
                buildRangeSpecification(criteria.getFiscalYear(), PurchaseOrder_.fiscalYear),
                buildStringSpecification(criteria.getPoTitle(), PurchaseOrder_.poTitle),
                buildStringSpecification(criteria.getAgencyAcronym(), PurchaseOrder_.agencyAcronym),
                buildStringSpecification(criteria.getAgencyName(), PurchaseOrder_.agencyName),
                buildRangeSpecification(criteria.getDcsLastModDttm(), PurchaseOrder_.dcsLastModDttm),
                buildRangeSpecification(criteria.getDcsRecCrtDttm(), PurchaseOrder_.dcsRecCrtDttm),
                buildRangeSpecification(criteria.getObjectId(), PurchaseOrder_.objectId),
                buildFullTextSearchSpecification(criteria.getSearch())
            );
        }
        return specification;
    }

    /**
     * Builds a Specification for PostgreSQL full-text search when search query is present.
     * Uses purchase_order_fts_match(tsvector, text) helper function.
     */
    private Specification<PurchaseOrder> buildFullTextSearchSpecification(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
            cb.isTrue(cb.function("purchase_order_fts_match", Boolean.class, root.get("searchVector"), cb.literal(searchQuery.trim())));
    }
}
