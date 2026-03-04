package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.domain.PassContract;
import io.argorand.poc.dcpass.repository.PassContractRepository;
import io.argorand.poc.dcpass.service.dto.PassContractDTO;
import io.argorand.poc.dcpass.service.mapper.PassContractMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.argorand.poc.dcpass.domain.PassContract}.
 */
@Service
@Transactional
public class PassContractService {

    private static final Logger LOG = LoggerFactory.getLogger(PassContractService.class);

    private final PassContractRepository passContractRepository;

    private final PassContractMapper passContractMapper;

    public PassContractService(PassContractRepository passContractRepository, PassContractMapper passContractMapper) {
        this.passContractRepository = passContractRepository;
        this.passContractMapper = passContractMapper;
    }

    /**
     * Save a passContract.
     *
     * @param passContractDTO the entity to save.
     * @return the persisted entity.
     */
    public PassContractDTO save(PassContractDTO passContractDTO) {
        LOG.debug("Request to save PassContract : {}", passContractDTO);
        PassContract passContract = passContractMapper.toEntity(passContractDTO);
        passContract = passContractRepository.save(passContract);
        return passContractMapper.toDto(passContract);
    }

    /**
     * Update a passContract.
     *
     * @param passContractDTO the entity to save.
     * @return the persisted entity.
     */
    public PassContractDTO update(PassContractDTO passContractDTO) {
        LOG.debug("Request to update PassContract : {}", passContractDTO);
        PassContract passContract = passContractMapper.toEntity(passContractDTO);
        passContract = passContractRepository.save(passContract);
        return passContractMapper.toDto(passContract);
    }

    /**
     * Partially update a passContract.
     *
     * @param passContractDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PassContractDTO> partialUpdate(PassContractDTO passContractDTO) {
        LOG.debug("Request to partially update PassContract : {}", passContractDTO);

        return passContractRepository
            .findById(passContractDTO.getId())
            .map(existingPassContract -> {
                passContractMapper.partialUpdate(existingPassContract, passContractDTO);

                return existingPassContract;
            })
            .map(passContractRepository::save)
            .map(passContractMapper::toDto);
    }

    /**
     * Get one passContract by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PassContractDTO> findOne(Long id) {
        LOG.debug("Request to get PassContract : {}", id);
        return passContractRepository.findById(id).map(passContractMapper::toDto);
    }

    /**
     * Delete the passContract by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PassContract : {}", id);
        passContractRepository.deleteById(id);
    }
}
