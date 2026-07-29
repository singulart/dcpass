package io.argorand.poc.dcpass.repository;

import io.argorand.poc.dcpass.domain.ItCommodityCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Read-only lookup for IT commodity codes used in spend reporting.
 */
@SuppressWarnings("unused")
@Repository
public interface ItCommodityCodeRepository extends JpaRepository<ItCommodityCode, Long> {
    @Query("select icc.code from ItCommodityCode icc order by icc.code")
    List<Long> findAllCodes();
}
