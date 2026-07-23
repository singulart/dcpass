package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.domain.*; // for static metamodels
import io.argorand.poc.dcpass.domain.PassPayment;
import io.argorand.poc.dcpass.repository.PassPaymentRepository;
import io.argorand.poc.dcpass.service.criteria.PassPaymentCriteria;
import io.argorand.poc.dcpass.service.dto.PassPaymentDTO;
import io.argorand.poc.dcpass.service.mapper.PassPaymentMapper;
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
 * Service for executing complex queries for {@link PassPayment} entities in the database.
 * The main input is a {@link PassPaymentCriteria} which gets converted to {@link Specification}.
 */
@Service
@Transactional(readOnly = true)
public class PassPaymentQueryService extends QueryService<PassPayment> {

    private static final Logger LOG = LoggerFactory.getLogger(PassPaymentQueryService.class);

    private final PassPaymentRepository passPaymentRepository;

    private final PassPaymentMapper passPaymentMapper;

    public PassPaymentQueryService(PassPaymentRepository passPaymentRepository, PassPaymentMapper passPaymentMapper) {
        this.passPaymentRepository = passPaymentRepository;
        this.passPaymentMapper = passPaymentMapper;
    }

    /**
     * Return a {@link Page} of {@link PassPaymentDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PassPaymentDTO> findByCriteria(PassPaymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PassPayment> specification = createSpecification(criteria);
        String ftsQuery = criteria != null ? criteria.getSearch() : null;
        Sort userSort = page.getSort();
        boolean hasFts = ftsQuery != null && !ftsQuery.isBlank();
        boolean hasUserSort = userSort.isSorted();

        Pageable pageForQuery = hasFts || hasUserSort ? PageRequest.of(page.getPageNumber(), page.getPageSize()) : page;
        Specification<PassPayment> querySpec = specification;
        if (hasFts || hasUserSort) {
            String trimmedFts = hasFts ? ftsQuery.trim() : null;
            querySpec = specification.and((root, query, cb) -> {
                if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                    List<Order> orders = new ArrayList<>();
                    if (hasFts) {
                        orders.add(
                            cb.desc(cb.function("pass_payment_fts_rank", Float.class, root.get("searchVector"), cb.literal(trimmedFts)))
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
        return passPaymentRepository.findAll(querySpec, pageForQuery).map(passPaymentMapper::toDto);
    }

    /**
     * Function to convert {@link PassPaymentCriteria} to a {@link Specification}.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PassPayment> createSpecification(PassPaymentCriteria criteria) {
        Specification<PassPayment> specification = Specification.unrestricted();
        if (criteria != null) {
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), PassPayment_.id),
                buildStringSpecification(criteria.getAgencyCode(), PassPayment_.agencyCode),
                buildStringSpecification(criteria.getAgencyAcronym(), PassPayment_.agencyAcronym),
                buildStringSpecification(criteria.getAgencyName(), PassPayment_.agencyName),
                buildStringSpecification(criteria.getContractNumber(), PassPayment_.contractNumber),
                buildStringSpecification(criteria.getSupplierName(), PassPayment_.supplierName),
                buildStringSpecification(criteria.getInvoiceNumber(), PassPayment_.invoiceNumber),
                buildStringSpecification(criteria.getPoNumber(), PassPayment_.poNumber),
                buildStringSpecification(criteria.getVoucherNumber(), PassPayment_.voucherNumber),
                buildRangeSpecification(criteria.getPaymentDate(), PassPayment_.paymentDate),
                buildRangeSpecification(criteria.getPaymentAmount(), PassPayment_.paymentAmount),
                buildRangeSpecification(criteria.getFiscalYear(), PassPayment_.fiscalYear),
                buildStringSpecification(criteria.getTransactionCode(), PassPayment_.transactionCode),
                buildStringSpecification(criteria.getPaymentType(), PassPayment_.paymentType),
                buildRangeSpecification(criteria.getInvoiceDate(), PassPayment_.invoiceDate),
                buildRangeSpecification(criteria.getEstPaymentDate(), PassPayment_.estPaymentDate),
                buildStringSpecification(criteria.getPaymentNumber(), PassPayment_.paymentNumber),
                buildRangeSpecification(criteria.getRecordUpdatedDate(), PassPayment_.recordUpdatedDate),
                buildRangeSpecification(criteria.getRecordCreated(), PassPayment_.recordCreated),
                buildRangeSpecification(criteria.getDcsRecCrtDttm(), PassPayment_.dcsRecCrtDttm),
                buildRangeSpecification(criteria.getDcsLastModDttm(), PassPayment_.dcsLastModDttm),
                buildRangeSpecification(criteria.getObjectId(), PassPayment_.objectId),
                buildFullTextSearchSpecification(criteria.getSearch())
            );
        }
        return specification;
    }

    /**
     * Builds a Specification for PostgreSQL full-text search when search query is present.
     * Uses pass_payment_fts_match(tsvector, text) helper function.
     * Query semantics (via dcpass_fts_query): spaces/OR → OR; AND or quotes → AND; mixed AND+OR → OR.
     */
    private Specification<PassPayment> buildFullTextSearchSpecification(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
            cb.isTrue(cb.function("pass_payment_fts_match", Boolean.class, root.get("searchVector"), cb.literal(searchQuery.trim())));
    }
}
