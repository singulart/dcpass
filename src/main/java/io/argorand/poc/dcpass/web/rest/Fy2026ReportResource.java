package io.argorand.poc.dcpass.web.rest;

import io.argorand.poc.dcpass.service.Fy2026ItSpendService;
import io.argorand.poc.dcpass.service.dto.Fy2026AgencySpendDTO;
import io.argorand.poc.dcpass.service.dto.Fy2026ContractSpendDTO;
import io.argorand.poc.dcpass.service.dto.Fy2026PoSpendDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for FY2026 IT spend drill-down charts.
 */
@RestController
@RequestMapping("/api/fy2026")
public class Fy2026ReportResource {

    private static final Logger LOG = LoggerFactory.getLogger(Fy2026ReportResource.class);

    private final Fy2026ItSpendService fy2026ItSpendService;

    public Fy2026ReportResource(Fy2026ItSpendService fy2026ItSpendService) {
        this.fy2026ItSpendService = fy2026ItSpendService;
    }

    /**
     * {@code GET /fy2026/it-spend-by-agency} : agency-level IT spend from the materialized view.
     */
    @GetMapping("/it-spend-by-agency")
    public ResponseEntity<List<Fy2026AgencySpendDTO>> getSpendByAgency() {
        LOG.debug("REST request to get FY2026 IT spend by agency");
        return ResponseEntity.ok(fy2026ItSpendService.getSpendByAgency());
    }

    /**
     * {@code GET /fy2026/it-spend-by-contract} : contract-level IT spend for an agency.
     */
    @GetMapping("/it-spend-by-contract")
    public ResponseEntity<List<Fy2026ContractSpendDTO>> getSpendByContract(@RequestParam String agencyAcronym) {
        LOG.debug("REST request to get FY2026 IT spend by contract for agency {}", agencyAcronym);
        return ResponseEntity.ok(fy2026ItSpendService.getSpendByContract(agencyAcronym));
    }

    /**
     * {@code GET /fy2026/it-spend-by-po} : PO-level IT spend for an agency and contract.
     * Omit {@code contractNumber} (or pass empty) to match rows where {@code purchase_order.contractnumber} is NULL.
     */
    @GetMapping("/it-spend-by-po")
    public ResponseEntity<List<Fy2026PoSpendDTO>> getSpendByPo(
        @RequestParam String agencyAcronym,
        @RequestParam(required = false) String contractNumber
    ) {
        LOG.debug("REST request to get FY2026 IT spend by PO for agency {} contract {}", agencyAcronym, contractNumber);
        String normalized = contractNumber == null || contractNumber.isBlank() ? null : contractNumber;
        return ResponseEntity.ok(fy2026ItSpendService.getSpendByPo(agencyAcronym, normalized));
    }
}
