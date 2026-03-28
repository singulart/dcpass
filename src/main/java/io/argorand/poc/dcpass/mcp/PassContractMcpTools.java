package io.argorand.poc.dcpass.mcp;

import io.argorand.poc.dcpass.service.PassContractQueryService;
import io.argorand.poc.dcpass.service.criteria.PassContractCriteria;
import io.argorand.poc.dcpass.service.dto.PassContractDTO;
import io.modelcontextprotocol.spec.McpSchema;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * MCP tools exposing pass contract operations to AI applications.
 * Uses tool-level errors (isError: true) for validation failures so agents receive
 * actionable messages and can retry with corrected parameters.
 */
@Component
public class PassContractMcpTools {

    private static final Logger log = LoggerFactory.getLogger(PassContractMcpTools.class);

    private static final int PAGE_SIZE = 20;
    private static final int MAX_PAGE = 99;
    private static final int MAX_SEARCH_LENGTH = 500;

    private final PassContractQueryService passContractQueryService;

    public PassContractMcpTools(PassContractQueryService passContractQueryService) {
        this.passContractQueryService = passContractQueryService;
    }

    @McpTool(
        name = "searchContractsInPASS",
        description = "Search District of Columbia Procurement Automated Support System (DCPASS) contracts dataset. Supports full-text search (q), min/max contract amounts and date ranges",
        metaProvider = ContractsWidgetToolMetaProvider.class,
        annotations = @McpTool.McpAnnotations(
            readOnlyHint = true,
            destructiveHint = false,
            idempotentHint = true,
            openWorldHint = false,
            title = "Search PASS contracts"
        )
    )
    public McpSchema.CallToolResult getAllPassContracts(
        @McpToolParam(description = "Search phrase", required = false) String q,
        @McpToolParam(description = "Award date 'from' (ISO-8601, e.g. 2026-01-01)", required = false) String awardDateFrom,
        @McpToolParam(description = "Award date 'to' (ISO-8601)", required = false) String awardDateTo,
        @McpToolParam(description = "Start date 'from' (ISO-8601)", required = false) String startDateFrom,
        @McpToolParam(description = "Start date 'to' (ISO-8601)", required = false) String startDateTo,
        @McpToolParam(description = "End date 'from' (ISO-8601)", required = false) String endDateFrom,
        @McpToolParam(description = "End date 'to' (ISO-8601)", required = false) String endDateTo,
        @McpToolParam(description = "Minimum contract amount", required = false) String minimumContractAmount,
        @McpToolParam(description = "Maximum contract amount", required = false) String maximumContractAmount,
        @McpToolParam(description = "Page index", required = false) Integer page
    ) {
        List<String> errors = new ArrayList<>();

        LocalDate parsedAwardFrom = parseDate("awardDateFrom", awardDateFrom, errors);
        LocalDate parsedAwardTo = parseDate("awardDateTo", awardDateTo, errors);
        LocalDate parsedStartFrom = parseDate("startDateFrom", startDateFrom, errors);
        LocalDate parsedStartTo = parseDate("startDateTo", startDateTo, errors);
        LocalDate parsedEndFrom = parseDate("endDateFrom", endDateFrom, errors);
        LocalDate parsedEndTo = parseDate("endDateTo", endDateTo, errors);

        BigDecimal parsedMinAmount = parseAmount("minimumContractAmount", minimumContractAmount, errors);
        BigDecimal parsedMaxAmount = parseAmount("maximumContractAmount", maximumContractAmount, errors);

        if (parsedMinAmount != null && parsedMaxAmount != null && parsedMinAmount.compareTo(parsedMaxAmount) > 0) {
            errors.add(
                "minimumContractAmount (" + parsedMinAmount + ") cannot be greater than maximumContractAmount (" + parsedMaxAmount + ")."
            );
        }

        int pageNumber = 0;
        if (page != null) {
            if (page < 0) {
                errors.add("page must be >= 0. Use 0 for the first page.");
            } else if (page > MAX_PAGE) {
                errors.add("page must be <= " + MAX_PAGE + " to avoid excessive result sets. Use pagination to browse further.");
            } else {
                pageNumber = page;
            }
        }

        if (q != null && !q.isBlank() && q.length() > MAX_SEARCH_LENGTH) {
            errors.add("Search phrase (q) must be at most " + MAX_SEARCH_LENGTH + " characters. Use a shorter or more specific query.");
        }

        if (!errors.isEmpty()) {
            String message =
                "Invalid input. " +
                String.join(" ", errors) +
                " Dates must be ISO-8601 (YYYY-MM-DD). Amounts must be valid numbers (e.g. 100000 or 5000000.50).";
            return McpSchema.CallToolResult.builder().isError(true).addTextContent(message).build();
        }

        PassContractCriteria criteria = new PassContractCriteria();
        criteria.setSearch(q);

        if (parsedAwardFrom != null) {
            criteria.awardDate().setGreaterThanOrEqual(parsedAwardFrom);
        }
        if (parsedAwardTo != null) {
            criteria.awardDate().setLessThanOrEqual(parsedAwardTo);
        }
        if (parsedStartFrom != null) {
            criteria.startDate().setGreaterThanOrEqual(parsedStartFrom);
        }
        if (parsedStartTo != null) {
            criteria.startDate().setLessThanOrEqual(parsedStartTo);
        }
        if (parsedEndFrom != null) {
            criteria.endDate().setGreaterThanOrEqual(parsedEndFrom);
        }
        if (parsedEndTo != null) {
            criteria.endDate().setLessThanOrEqual(parsedEndTo);
        }
        if (parsedMinAmount != null) {
            criteria.contractAmount().setGreaterThanOrEqual(parsedMinAmount);
        }
        if (parsedMaxAmount != null) {
            criteria.contractAmount().setLessThanOrEqual(parsedMaxAmount);
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));

        if (q != null && !q.isBlank()) {
            String escapedQuery = q.trim().replace("'", "''");
            Sort relevanceSort = org.springframework.data.jpa.domain.JpaSort.unsafe(
                "pass_contract_fts_rank(searchVector, '" + escapedQuery + "') DESC"
            );
            pageable = PageRequest.of(pageNumber, PAGE_SIZE, relevanceSort.and(Sort.by(Sort.Direction.ASC, "id")));
        }

        long t0 = System.nanoTime();
        log.info("MCP searchContractsInPASS start q={} page={}", q, pageNumber);

        long tQuery = System.nanoTime();
        var pageResult = passContractQueryService.findByCriteria(criteria, pageable);
        long queryMs = (System.nanoTime() - tQuery) / 1_000_000L;
        List<PassContractDTO> contracts = pageResult.getContent();
        long totalItems = pageResult.getTotalElements();
        Map<String, Object> apiParams = buildApiParams(
            q,
            parsedAwardFrom,
            parsedAwardTo,
            parsedStartFrom,
            parsedStartTo,
            parsedEndFrom,
            parsedEndTo,
            parsedMinAmount,
            parsedMaxAmount,
            pageNumber,
            PAGE_SIZE
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("contracts", contracts);
        result.put("page", pageNumber);
        result.put("totalItems", totalItems);
        result.put("size", PAGE_SIZE);
        result.put("apiParams", apiParams);
        long totalMs = (System.nanoTime() - t0) / 1_000_000L;
        log.info(
            "MCP searchContractsInPASS ok queryMs={} totalMs={} rows={} totalItems={}",
            queryMs,
            totalMs,
            contracts.size(),
            totalItems
        );
        String modelText = buildModelFacingSummary(contracts.size(), totalItems, pageNumber, PAGE_SIZE);
        Map<String, Object> resultMeta = buildResultMeta(contracts);
        return McpSchema.CallToolResult.builder().structuredContent(result).addTextContent(modelText).meta(resultMeta).build();
    }

    /**
     * Concise natural-language summary for the model. Full rows live in {@code structuredContent} (and optional
     * {@code _meta.contractsById}) per MCP Apps patterns; it is not duplicated as JSON in {@code content}.
     */
    private static String buildModelFacingSummary(int rowCount, long totalItems, int pageIndex, int pageSize) {
        if (totalItems == 0) {
            return "No PASS contracts matched these filters.";
        }
        long totalPages = pageSize <= 0 ? 1 : (totalItems + pageSize - 1) / pageSize;
        int humanPage = pageIndex + 1;
        return String.format(
            "Found %d matching contract(s) in PASS (%d on this page; page %d of %d, %d per page). Use structured output or the embedded widget for full rows.",
            totalItems,
            rowCount,
            humanPage,
            totalPages,
            pageSize
        );
    }

    /** Supplementary keyed view for embedded UIs, analogous to MCP Apps examples (e.g. {@code allAnimalsById}). */
    private static Map<String, Object> buildResultMeta(List<PassContractDTO> contracts) {
        Map<String, Object> byId = new LinkedHashMap<>();
        for (PassContractDTO c : contracts) {
            if (c.getId() != null) {
                byId.put(String.valueOf(c.getId()), c);
            }
        }
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("contractsById", byId);
        return meta;
    }

    private static LocalDate parseDate(String paramName, String value, List<String> errors) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeException e) {
            errors.add(paramName + " '" + value + "' is not a valid date. Use ISO-8601 format (YYYY-MM-DD), e.g. 2026-01-15.");
            return null;
        }
    }

    private static Map<String, Object> buildApiParams(
        String q,
        LocalDate awardFrom,
        LocalDate awardTo,
        LocalDate startFrom,
        LocalDate startTo,
        LocalDate endFrom,
        LocalDate endTo,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        int page,
        int size
    ) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sort", "id,asc");
        if (q != null && !q.isBlank()) {
            params.put("q", q.trim());
        }
        if (awardFrom != null) {
            params.put("awardDate.greaterThanOrEqual", awardFrom.format(fmt));
        }
        if (awardTo != null) {
            params.put("awardDate.lessThanOrEqual", awardTo.format(fmt));
        }
        if (startFrom != null) {
            params.put("startDate.greaterThanOrEqual", startFrom.format(fmt));
        }
        if (startTo != null) {
            params.put("startDate.lessThanOrEqual", startTo.format(fmt));
        }
        if (endFrom != null) {
            params.put("endDate.greaterThanOrEqual", endFrom.format(fmt));
        }
        if (endTo != null) {
            params.put("endDate.lessThanOrEqual", endTo.format(fmt));
        }
        if (minAmount != null) {
            params.put("contractAmount.greaterThanOrEqual", minAmount.toString());
        }
        if (maxAmount != null) {
            params.put("contractAmount.lessThanOrEqual", maxAmount.toString());
        }
        return params;
    }

    private static BigDecimal parseAmount(String paramName, String value, List<String> errors) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.signum() < 0) {
                errors.add(paramName + " cannot be negative.");
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            errors.add(paramName + " '" + value + "' is not a valid number. Use a numeric value, e.g. 100000 or 5000000.50.");
            return null;
        }
    }
}
