package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.domain.*; // for static metamodels
import io.argorand.poc.dcpass.domain.PassContract;
import io.argorand.poc.dcpass.repository.PassContractRepository;
import io.argorand.poc.dcpass.service.criteria.PassContractCriteria;
import io.argorand.poc.dcpass.service.dto.PassContractDTO;
import io.argorand.poc.dcpass.service.mapper.PassContractMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PassContract} entities in the database.
 * The main input is a {@link PassContractCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PassContractDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PassContractQueryService extends QueryService<PassContract> {

    private static final Logger LOG = LoggerFactory.getLogger(PassContractQueryService.class);

    private final PassContractRepository passContractRepository;

    private final PassContractMapper passContractMapper;

    public PassContractQueryService(PassContractRepository passContractRepository, PassContractMapper passContractMapper) {
        this.passContractRepository = passContractRepository;
        this.passContractMapper = passContractMapper;
    }

    /**
     * Return a {@link Page} of {@link PassContractDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PassContractDTO> findByCriteria(PassContractCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PassContract> specification = createSpecification(criteria);
        return passContractRepository.findAll(specification, page).map(passContractMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PassContractCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PassContract> specification = createSpecification(criteria);
        return passContractRepository.count(specification);
    }

    /**
     * Function to convert {@link PassContractCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PassContract> createSpecification(PassContractCriteria criteria) {
        Specification<PassContract> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), PassContract_.id),
                buildStringSpecification(criteria.getProcurementMethodDescription(), PassContract_.procurementMethodDescription),
                buildStringSpecification(criteria.getAgencyAcronym(), PassContract_.agencyAcronym),
                buildStringSpecification(criteria.getAgencyName(), PassContract_.agencyName),
                buildRangeSpecification(criteria.getRowId(), PassContract_.rowId),
                buildStringSpecification(criteria.getAgency(), PassContract_.agency),
                buildRangeSpecification(criteria.getAwardDate(), PassContract_.awardDate),
                buildRangeSpecification(criteria.getContractAmount(), PassContract_.contractAmount),
                buildRangeSpecification(criteria.getEndDate(), PassContract_.endDate),
                buildStringSpecification(criteria.getContractNumber(), PassContract_.contractNumber),
                buildRangeSpecification(criteria.getStartDate(), PassContract_.startDate),
                buildStringSpecification(criteria.getContractStatus(), PassContract_.contractStatus),
                buildStringSpecification(criteria.getTitle(), PassContract_.title),
                buildStringSpecification(criteria.getContractingOfficer(), PassContract_.contractingOfficer),
                buildRangeSpecification(criteria.getFiscalYear(), PassContract_.fiscalYear),
                buildStringSpecification(criteria.getMarketType(), PassContract_.marketType),
                buildStringSpecification(criteria.getCommodityCode(), PassContract_.commodityCode),
                buildStringSpecification(criteria.getCommodityDescription(), PassContract_.commodityDescription),
                buildRangeSpecification(criteria.getCurrentOptionPeriod(), PassContract_.currentOptionPeriod),
                buildRangeSpecification(criteria.getTotalOptionPeriods(), PassContract_.totalOptionPeriods),
                buildStringSpecification(criteria.getSupplier(), PassContract_.supplier),
                buildStringSpecification(criteria.getDescription(), PassContract_.description),
                buildStringSpecification(criteria.getContractTypeDescription(), PassContract_.contractTypeDescription),
                buildStringSpecification(criteria.getContractingOfficerEmail(), PassContract_.contractingOfficerEmail),
                buildStringSpecification(criteria.getVendorAddress(), PassContract_.vendorAddress),
                buildStringSpecification(criteria.getVendorCity(), PassContract_.vendorCity),
                buildStringSpecification(criteria.getVendorState(), PassContract_.vendorState),
                buildStringSpecification(criteria.getVendorZip(), PassContract_.vendorZip),
                buildStringSpecification(criteria.getPublishedVersionId(), PassContract_.publishedVersionId),
                buildStringSpecification(criteria.getDocumentVersion(), PassContract_.documentVersion),
                buildRangeSpecification(criteria.getLastModified(), PassContract_.lastModified),
                buildStringSpecification(criteria.getContractingSplst(), PassContract_.contractingSplst),
                buildStringSpecification(criteria.getContractingSplstEmail(), PassContract_.contractingSplstEmail),
                buildStringSpecification(criteria.getSource(), PassContract_.source),
                buildStringSpecification(criteria.getContractDetailsLink(), PassContract_.contractDetailsLink),
                buildStringSpecification(criteria.getContractAdministratorName(), PassContract_.contractAdministratorName),
                buildStringSpecification(criteria.getContractAdministratorEmail(), PassContract_.contractAdministratorEmail),
                buildStringSpecification(criteria.getContractAdministratorPhone(), PassContract_.contractAdministratorPhone),
                buildStringSpecification(criteria.getContractOfficerPhone(), PassContract_.contractOfficerPhone),
                buildStringSpecification(criteria.getCwInternalId(), PassContract_.cwInternalId),
                buildStringSpecification(criteria.getCorporatePhone(), PassContract_.corporatePhone),
                buildStringSpecification(criteria.getCorporateEmailAddress(), PassContract_.corporateEmailAddress),
                buildRangeSpecification(criteria.getRecCreatedDate(), PassContract_.recCreatedDate),
                buildRangeSpecification(criteria.getRecUpdatedDate(), PassContract_.recUpdatedDate),
                buildRangeSpecification(criteria.getDcsLastModDttm(), PassContract_.dcsLastModDttm),
                buildRangeSpecification(criteria.getObjectId(), PassContract_.objectId)
            );
        }
        return specification;
    }
}
