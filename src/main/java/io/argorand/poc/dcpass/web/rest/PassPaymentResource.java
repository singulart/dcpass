package io.argorand.poc.dcpass.web.rest;

import io.argorand.poc.dcpass.repository.PassPaymentRepository;
import io.argorand.poc.dcpass.service.PassPaymentQueryService;
import io.argorand.poc.dcpass.service.PassPaymentService;
import io.argorand.poc.dcpass.service.criteria.PassPaymentCriteria;
import io.argorand.poc.dcpass.service.dto.PassPaymentDTO;
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
 * REST controller for managing {@link io.argorand.poc.dcpass.domain.PassPayment}.
 */
@RestController
@RequestMapping("/api/pass-payments")
public class PassPaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(PassPaymentResource.class);

    private static final String ENTITY_NAME = "passPayment";

    @Value("${jhipster.clientApp.name:dcpass}")
    private String applicationName;

    private final PassPaymentService passPaymentService;

    private final PassPaymentRepository passPaymentRepository;

    private final PassPaymentQueryService passPaymentQueryService;

    public PassPaymentResource(
        PassPaymentService passPaymentService,
        PassPaymentRepository passPaymentRepository,
        PassPaymentQueryService passPaymentQueryService
    ) {
        this.passPaymentService = passPaymentService;
        this.passPaymentRepository = passPaymentRepository;
        this.passPaymentQueryService = passPaymentQueryService;
    }

    /**
     * {@code POST  /pass-payments} : Create a new passPayment.
     *
     * @param passPaymentDTO the passPaymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new passPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the passPayment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PassPaymentDTO> createPassPayment(@RequestBody PassPaymentDTO passPaymentDTO) throws URISyntaxException {
        LOG.debug("REST request to save PassPayment : {}", passPaymentDTO);
        if (passPaymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new passPayment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        passPaymentDTO = passPaymentService.save(passPaymentDTO);
        return ResponseEntity.created(new URI("/api/pass-payments/" + passPaymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, passPaymentDTO.getId().toString()))
            .body(passPaymentDTO);
    }

    /**
     * {@code PUT  /pass-payments/:id} : Updates an existing passPayment.
     *
     * @param id the id of the passPaymentDTO to save.
     * @param passPaymentDTO the passPaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated passPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the passPaymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the passPaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PassPaymentDTO> updatePassPayment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PassPaymentDTO passPaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PassPayment : {}, {}", id, passPaymentDTO);
        if (passPaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, passPaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!passPaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        passPaymentDTO = passPaymentService.update(passPaymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, passPaymentDTO.getId().toString()))
            .body(passPaymentDTO);
    }

    /**
     * {@code GET  /pass-payments} : get all the passPayments.
     *
     * @param q optional full-text search query (searches agency, supplier, invoice, PO, voucher, etc.).
     *          Spaces and OR match any term; AND or quotes require all terms; mixed AND+OR flattens to OR.
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of passPayments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PassPaymentDTO>> getAllPassPayments(
        @RequestParam(name = "q", required = false) String q,
        PassPaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PassPayments by criteria: {}", criteria);

        criteria.setSearch(q);
        Page<PassPaymentDTO> page = passPaymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pass-payments/:id} : get the "id" passPayment.
     *
     * @param id the id of the passPaymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the passPaymentDTO,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PassPaymentDTO> getPassPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PassPayment : {}", id);
        Optional<PassPaymentDTO> passPaymentDTO = passPaymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(passPaymentDTO);
    }

    /**
     * {@code DELETE  /pass-payments/:id} : delete the "id" passPayment.
     *
     * @param id the id of the passPaymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PassPayment : {}", id);
        passPaymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
