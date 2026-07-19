package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.domain.PurchaseOrder;
import io.argorand.poc.dcpass.repository.PurchaseOrderRepository;
import io.argorand.poc.dcpass.service.dto.PurchaseOrderDTO;
import io.argorand.poc.dcpass.service.mapper.PurchaseOrderMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.argorand.poc.dcpass.domain.PurchaseOrder}.
 */
@Service
@Transactional
public class PurchaseOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderService.class);

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderMapper purchaseOrderMapper) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    /**
     * Save a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOrderDTO save(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to save PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    /**
     * Update a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOrderDTO update(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to update PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    /**
     * Get one purchaseOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderDTO> findOne(Long id) {
        LOG.debug("Request to get PurchaseOrder : {}", id);
        return purchaseOrderRepository.findById(id).map(purchaseOrderMapper::toDto);
    }

    /**
     * Delete the purchaseOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PurchaseOrder : {}", id);
        purchaseOrderRepository.deleteById(id);
    }
}
