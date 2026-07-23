package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.PassPayment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PassPayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PassPaymentRepository extends JpaRepository<PassPayment, Long>, JpaSpecificationExecutor<PassPayment> {}
