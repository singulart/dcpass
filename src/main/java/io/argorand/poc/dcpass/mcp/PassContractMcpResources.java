package io.argorand.poc.dcpass.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

/**
 * MCP resources exposing DCPASS metadata and information to AI applications.
 *
 * <p>The ChatGPT / OpenAI Apps widget template must be registered here with {@code text/html;profile=mcp-app}
 * so the host can resolve {@code openai/outputTemplate} / {@code _meta.ui.resourceUri} from the tool.
 */
@Component
public class PassContractMcpResources {

    private final ObjectMapper objectMapper;
    private final ContractsWidgetSpaHtmlProvider contractsWidgetSpaHtmlProvider;

    public PassContractMcpResources(ObjectMapper objectMapper, ContractsWidgetSpaHtmlProvider contractsWidgetSpaHtmlProvider) {
        this.objectMapper = objectMapper;
        this.contractsWidgetSpaHtmlProvider = contractsWidgetSpaHtmlProvider;
    }

    /**
     * MCP Apps UI template: full JHipster SPA shell with absolute {@code base href} so scripts load from the public
     * origin inside the host sandbox; {@code __DCPASS_EMBED_CONTRACTS_WIDGET__} triggers an initial navigation to
     * {@code /contracts-widget} (see {@code app.config.ts}).
     */
    @McpResource(
        uri = ContractsWidgetToolMetaProvider.WIDGET_RESOURCE_URI,
        name = "PASS contracts widget",
        title = "PASS contracts widget",
        description = "Embeds the DCPASS contracts browser for ChatGPT (OpenAI Apps SDK).",
        mimeType = "text/html;profile=mcp-app",
        metaProvider = ContractsWidgetResourceMetaProvider.class
    )
    public String getContractsWidgetMcpTemplate() {
        return contractsWidgetSpaHtmlProvider.buildEmbeddedWidgetHtml();
    }

    @McpResource(
        uri = "dcpass://about",
        name = "DCPASS MCP Server Info",
        description = "About the District of Columbia Procurement Automated Support System (DCPASS) MCP server"
    )
    public String getAbout() {
        try {
            Map<String, Object> info = Map.of(
                "name",
                "dcpass-mcp",
                "description",
                "MCP server for searching 50,000 DC Government contracts",
                "capabilities",
                Map.of(
                    "tools",
                    Map.of("searchContractsInPASS", "Search DCPASS contracts with full-text search, date filters, and pagination"),
                    "resources",
                    Map.of(
                        "dcpass://about",
                        "This MCP server information",
                        ContractsWidgetToolMetaProvider.WIDGET_RESOURCE_URI,
                        "Widget template"
                    )
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
