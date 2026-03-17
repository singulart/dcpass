package io.argorand.poc.dcpass.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

/**
 * MCP resources exposing DCPASS metadata and information to AI applications.
 */
@Component
public class PassContractMcpResources {

    private final ObjectMapper objectMapper;

    public PassContractMcpResources(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @McpResource(
        uri = "dcpass://about",
        name = "DCPASS MCP Server Info",
        description = "Information about the District of Columbia Procurement Automated Support System (DCPASS) MCP server and available capabilities."
    )
    public String getAbout() {
        try {
            Map<String, Object> info = Map.of(
                "name",
                "dcpass-mcp",
                "description",
                "MCP server for searching DC Government procurement contracts",
                "capabilities",
                Map.of(
                    "tools",
                    Map.of("searchContractsInPASS", "Search DCPASS contracts with full-text search, date filters, and pagination"),
                    "resources",
                    Map.of("dcpass://about", "This server information")
                ),
                "usage",
                "Use the searchContractsInPASS tool to query PASS contracts. Supports parameters: q (search phrase), awardDateFrom/To, startDateFrom/To, endDateFrom/To, page."
            );
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(info);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize DCPASS info", e);
        }
    }
}
