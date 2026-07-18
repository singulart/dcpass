package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.PassContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Custom queries for grouping pass_contract rows by contract number.
 */
public interface PassContractRepositoryCustom {
    /**
     * Returns one representative row id per distinct contract number matching the specification.
     * Rows with a null contract number are each treated as their own group.
     *
     * @param spec matching row filters (commodity / FTS / etc. applied at row level before grouping)
     * @param pageable pagination and sort (applied at the contract-number group level)
     * @param ftsQuery when non-blank, groups are ordered by best FTS rank first
     * @return page of representative entity ids
     */
    Page<Long> findDistinctRepresentativeIds(Specification<PassContract> spec, Pageable pageable, String ftsQuery);
}
