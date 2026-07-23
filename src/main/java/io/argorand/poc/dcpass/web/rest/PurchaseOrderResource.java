package io.argorand.poc.dcpass.web.rest;

import io.argorand.poc.dcpass.repository.PurchaseOrderRepository;
import io.argorand.poc.dcpass.service.PurchaseOrderPaymentSummaryService;
import io.argorand.poc.dcpass.service.PurchaseOrderQueryService;
import io.argorand.poc.dcpass.service.PurchaseOrderService;
import io.argorand.poc.dcpass.service.criteria.PurchaseOrderCriteria;
import io.argorand.poc.dcpass.service.dto.PurchaseOrderDTO;
import io.argorand.poc.dcpass.service.dto.PurchaseOrderPaymentSummaryDTO;
import io.argorand.poc.dcpass.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.argorand.poc.dcpass.domain.PurchaseOrder}.
 */
@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderResource.class);

    private static final String ENTITY_NAME = "purchaseOrder";

    @Value("${jhipster.clientApp.name:dcpass}")
    private String applicationName;

    private final PurchaseOrderService purchaseOrderService;

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderQueryService purchaseOrderQueryService;

    private final PurchaseOrderPaymentSummaryService purchaseOrderPaymentSummaryService;

    public PurchaseOrderResource(
        PurchaseOrderService purchaseOrderService,
        PurchaseOrderRepository purchaseOrderRepository,
        PurchaseOrderQueryService purchaseOrderQueryService,
        PurchaseOrderPaymentSummaryService purchaseOrderPaymentSummaryService
    ) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderQueryService = purchaseOrderQueryService;
        this.purchaseOrderPaymentSummaryService = purchaseOrderPaymentSummaryService;
    }

    /**
     * {@code POST  /purchase-orders} : Create a new purchaseOrder.
     *
     * @param purchaseOrderDTO the purchaseOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseOrderDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) throws URISyntaxException {
        LOG.debug("REST request to save PurchaseOrder : {}", purchaseOrderDTO);
        if (purchaseOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        purchaseOrderDTO = purchaseOrderService.save(purchaseOrderDTO);
        return ResponseEntity.created(new URI("/api/purchase-orders/" + purchaseOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, purchaseOrderDTO.getId().toString()))
            .body(purchaseOrderDTO);
    }

    /**
     * {@code PUT  /purchase-orders/:id} : Updates an existing purchaseOrder.
     *
     * @param id the id of the purchaseOrderDTO to save.
     * @param purchaseOrderDTO the purchaseOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseOrderDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> updatePurchaseOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseOrderDTO purchaseOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PurchaseOrder : {}, {}", id, purchaseOrderDTO);
        if (purchaseOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        purchaseOrderDTO = purchaseOrderService.update(purchaseOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, purchaseOrderDTO.getId().toString()))
            .body(purchaseOrderDTO);
    }

    /**
     * {@code GET  /purchase-orders} : get all the purchaseOrders.
     *
     * @param q optional full-text search query (searches title, agency, supplier, etc.).
     *          Spaces and OR match any term; AND or quotes require all terms; mixed AND+OR flattens to OR.
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders(
        @RequestParam(name = "q", required = false) String q,
        PurchaseOrderCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PurchaseOrders by criteria: {}", criteria);

        criteria.setSearch(q);
        Page<PurchaseOrderDTO> page = purchaseOrderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-orders/payment-summary/:poNumber} : total dollars paid for a purchase order.
     * <p>
     * Sums {@code paymentamount} on {@code pass_payment} rows whose {@code ponumber} matches.
     *
     * @param poNumber the purchase order number (e.g. {@code PO123456}).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the payment summary in body.
     */
    @GetMapping("/payment-summary/{poNumber}")
    public ResponseEntity<PurchaseOrderPaymentSummaryDTO> getPaymentSummary(@PathVariable("poNumber") String poNumber) {
        LOG.debug("REST request to get payment summary for PO number : {}", poNumber);
        return ResponseEntity.ok(purchaseOrderPaymentSummaryService.getSummaryByPoNumber(poNumber));
    }

    /**
     * {@code GET  /purchase-orders/:id} : get the "id" purchaseOrder.
     *
     * @param id the id of the purchaseOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseOrderDTO,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getPurchaseOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PurchaseOrder : {}", id);
        Optional<PurchaseOrderDTO> purchaseOrderDTO = purchaseOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseOrderDTO);
    }

    /**
     * {@code DELETE  /purchase-orders/:id} : delete the "id" purchaseOrder.
     *
     * @param id the id of the purchaseOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PurchaseOrder : {}", id);
        purchaseOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
