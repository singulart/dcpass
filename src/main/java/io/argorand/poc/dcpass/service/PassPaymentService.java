package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.domain.PassPayment;
import io.argorand.poc.dcpass.repository.PassPaymentRepository;
import io.argorand.poc.dcpass.service.dto.PassPaymentDTO;
import io.argorand.poc.dcpass.service.mapper.PassPaymentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.argorand.poc.dcpass.domain.PassPayment}.
 */
@Service
@Transactional
public class PassPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PassPaymentService.class);

    private final PassPaymentRepository passPaymentRepository;

    private final PassPaymentMapper passPaymentMapper;

    public PassPaymentService(PassPaymentRepository passPaymentRepository, PassPaymentMapper passPaymentMapper) {
        this.passPaymentRepository = passPaymentRepository;
        this.passPaymentMapper = passPaymentMapper;
    }

    /**
     * Save a passPayment.
     *
     * @param passPaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public PassPaymentDTO save(PassPaymentDTO passPaymentDTO) {
        LOG.debug("Request to save PassPayment : {}", passPaymentDTO);
        PassPayment passPayment = passPaymentMapper.toEntity(passPaymentDTO);
        passPayment = passPaymentRepository.save(passPayment);
        return passPaymentMapper.toDto(passPayment);
    }

    /**
     * Update a passPayment.
     *
     * @param passPaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public PassPaymentDTO update(PassPaymentDTO passPaymentDTO) {
        LOG.debug("Request to update PassPayment : {}", passPaymentDTO);
        PassPayment passPayment = passPaymentMapper.toEntity(passPaymentDTO);
        passPayment = passPaymentRepository.save(passPayment);
        return passPaymentMapper.toDto(passPayment);
    }

    /**
     * Get one passPayment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PassPaymentDTO> findOne(Long id) {
        LOG.debug("Request to get PassPayment : {}", id);
        return passPaymentRepository.findById(id).map(passPaymentMapper::toDto);
    }

    /**
     * Delete the passPayment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PassPayment : {}", id);
        passPaymentRepository.deleteById(id);
    }
}
