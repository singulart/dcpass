package io.argorand.poc.dcpass.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.argorand.poc.dcpass.service.PassContractQueryService;
import io.argorand.poc.dcpass.service.criteria.PassContractCriteria;
import io.argorand.poc.dcpass.service.dto.PassContractDTO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.compiler.STParser.templateAndEOF_return;

/**
 * MCP tools exposing pass contract operations to AI applications.
 */
@Component
public class PassContractMcpTools {

    private static final int PAGE_SIZE = 20;

    private final PassContractQueryService passContractQueryService;
    private final ObjectMapper objectMapper;

    public PassContractMcpTools(PassContractQueryService passContractQueryService, ObjectMapper objectMapper) {
        this.passContractQueryService = passContractQueryService;
        this.objectMapper = objectMapper;
    }

    @McpTool(
        name = "searchContractsInPASS",
        description = "Search District of Columbia Procurement Automated Support System (DCPASS) contracts dataset. Supports full-text search (q), date range filters (awardDate, startDate, endDate), and pagination (page).",
        annotations = @McpTool.McpAnnotations(
            readOnlyHint = true,
            destructiveHint = false,
            idempotentHint = true,
            openWorldHint = false,
            title = "Search PASS contracts"
        )
    )
    public String getAllPassContracts(
        @McpToolParam(description = "Search phrase", required = false) String q,
        @McpToolParam(description = "Award date 'from' (ISO-8601, e.g. 2024-01-01)", required = false) String awardDateFrom,
        @McpToolParam(description = "Award date 'to' (ISO-8601)", required = false) String awardDateTo,
        @McpToolParam(description = "Start date 'from' (ISO-8601)", required = false) String startDateFrom,
        @McpToolParam(description = "Start date 'to' (ISO-8601)", required = false) String startDateTo,
        @McpToolParam(description = "End date 'from' (ISO-8601)", required = false) String endDateFrom,
        @McpToolParam(description = "End date 'to' (ISO-8601)", required = false) String endDateTo,
        @McpToolParam(description = "Page index", required = false) Integer page
    ) {
        PassContractCriteria criteria = new PassContractCriteria();
        criteria.setSearch(q);

        if (awardDateFrom != null) {
            criteria.awardDate().setGreaterThanOrEqual(LocalDate.parse(awardDateFrom));
        }
        if (awardDateTo != null) {
            criteria.awardDate().setLessThanOrEqual(LocalDate.parse(awardDateTo));
        }
        if (startDateFrom != null) {
            criteria.startDate().setGreaterThanOrEqual(LocalDate.parse(startDateFrom));
        }
        if (startDateTo != null) {
            criteria.startDate().setLessThanOrEqual(LocalDate.parse(startDateTo));
        }
        if (endDateFrom != null) {
            criteria.endDate().setGreaterThanOrEqual(LocalDate.parse(endDateFrom));
        }
        if (endDateTo != null) {
            criteria.endDate().setLessThanOrEqual(LocalDate.parse(endDateTo));
        }

        int pageNumber = (page != null && page >= 0) ? page : 0;
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "lastModified"));

        if (q != null && !q.isBlank()) {
            String escapedQuery = q.trim().replace("'", "''");
            Sort relevanceSort = org.springframework.data.jpa.domain.JpaSort.unsafe(
                "pass_contract_fts_rank(searchVector, '" + escapedQuery + "') DESC"
            );
            pageable = PageRequest.of(pageNumber, PAGE_SIZE, relevanceSort);
        }

        List<PassContractDTO> contracts = passContractQueryService.findByCriteria(criteria, pageable).getContent();

        try {
            return objectMapper.writeValueAsString(contracts);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize pass contracts", e);
        }
    }
}
