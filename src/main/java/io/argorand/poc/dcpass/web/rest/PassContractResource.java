package io.argorand.poc.dcpass.web.rest;

import io.argorand.poc.dcpass.repository.PassContractRepository;
import io.argorand.poc.dcpass.service.PassContractQueryService;
import io.argorand.poc.dcpass.service.PassContractService;
import io.argorand.poc.dcpass.service.criteria.PassContractCriteria;
import io.argorand.poc.dcpass.service.dto.PassContractDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.argorand.poc.dcpass.domain.PassContract}.
 */
@RestController
@RequestMapping("/api/pass-contracts")
public class PassContractResource {

    private static final Logger LOG = LoggerFactory.getLogger(PassContractResource.class);

    private static final String ENTITY_NAME = "passContract";

    @Value("${jhipster.clientApp.name:dcpass}")
    private String applicationName;

    private final PassContractService passContractService;

    private final PassContractRepository passContractRepository;

    private final PassContractQueryService passContractQueryService;

    public PassContractResource(
        PassContractService passContractService,
        PassContractRepository passContractRepository,
        PassContractQueryService passContractQueryService
    ) {
        this.passContractService = passContractService;
        this.passContractRepository = passContractRepository;
        this.passContractQueryService = passContractQueryService;
    }

    /**
     * {@code POST  /pass-contracts} : Create a new passContract.
     *
     * @param passContractDTO the passContractDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new passContractDTO, or with status {@code 400 (Bad Request)} if the passContract has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PassContractDTO> createPassContract(@RequestBody PassContractDTO passContractDTO) throws URISyntaxException {
        LOG.debug("REST request to save PassContract : {}", passContractDTO);
        if (passContractDTO.getId() != null) {
            throw new BadRequestAlertException("A new passContract cannot already have an ID", ENTITY_NAME, "idexists");
        }
        passContractDTO = passContractService.save(passContractDTO);
        return ResponseEntity.created(new URI("/api/pass-contracts/" + passContractDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, passContractDTO.getId().toString()))
            .body(passContractDTO);
    }

    /**
     * {@code PUT  /pass-contracts/:id} : Updates an existing passContract.
     *
     * @param id the id of the passContractDTO to save.
     * @param passContractDTO the passContractDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated passContractDTO,
     * or with status {@code 400 (Bad Request)} if the passContractDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the passContractDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PassContractDTO> updatePassContract(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PassContractDTO passContractDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PassContract : {}, {}", id, passContractDTO);
        if (passContractDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, passContractDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!passContractRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        passContractDTO = passContractService.update(passContractDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, passContractDTO.getId().toString()))
            .body(passContractDTO);
    }

    /**
     * {@code PATCH  /pass-contracts/:id} : Partial updates given fields of an existing passContract, field will ignore if it is null
     *
     * @param id the id of the passContractDTO to save.
     * @param passContractDTO the passContractDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated passContractDTO,
     * or with status {@code 400 (Bad Request)} if the passContractDTO is not valid,
     * or with status {@code 404 (Not Found)} if the passContractDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the passContractDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PassContractDTO> partialUpdatePassContract(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PassContractDTO passContractDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PassContract partially : {}, {}", id, passContractDTO);
        if (passContractDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, passContractDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!passContractRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PassContractDTO> result = passContractService.partialUpdate(passContractDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, passContractDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pass-contracts} : get all the passContracts.
     *
     * @param q optional full-text search query (searches title, description, agency, supplier, etc.)
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of passContracts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PassContractDTO>> getAllPassContracts(
        @RequestParam(name = "q", required = false) String q,
        PassContractCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PassContracts by criteria: {}", criteria);

        criteria.setSearch(q);
        Pageable pageableToUse = pageable;
        if (q != null && !q.isBlank()) {
            String escapedQuery = q.trim().replace("'", "''");
            Sort relevanceSort = JpaSort.unsafe("pass_contract_fts_rank(search_vector, '" + escapedQuery + "') DESC");
            pageableToUse = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isSorted() ? relevanceSort.and(pageable.getSort()) : relevanceSort
            );
        }

        Page<PassContractDTO> page = passContractQueryService.findByCriteria(criteria, pageableToUse);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pass-contracts/count} : count all the passContracts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPassContracts(PassContractCriteria criteria) {
        LOG.debug("REST request to count PassContracts by criteria: {}", criteria);
        return ResponseEntity.ok().body(passContractQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /pass-contracts/:id} : get the "id" passContract.
     *
     * @param id the id of the passContractDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the passContractDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PassContractDTO> getPassContract(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PassContract : {}", id);
        Optional<PassContractDTO> passContractDTO = passContractService.findOne(id);
        return ResponseUtil.wrapOrNotFound(passContractDTO);
    }

    /**
     * {@code DELETE  /pass-contracts/:id} : delete the "id" passContract.
     *
     * @param id the id of the passContractDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassContract(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PassContract : {}", id);
        passContractService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
