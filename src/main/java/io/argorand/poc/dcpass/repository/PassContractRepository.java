package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.PassContract;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PassContract entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PassContractRepository extends JpaRepository<PassContract, Long>, JpaSpecificationExecutor<PassContract> {}
