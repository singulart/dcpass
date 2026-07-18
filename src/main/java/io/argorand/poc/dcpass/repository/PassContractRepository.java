package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.PassContract;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PassContract entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PassContractRepository
    extends JpaRepository<PassContract, Long>, JpaSpecificationExecutor<PassContract>, PassContractRepositoryCustom
{
    List<PassContract> findByContractNumberIn(Collection<String> contractNumbers);

    List<PassContract> findByContractNumber(String contractNumber);
}
