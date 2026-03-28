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
 * so the host can resolve {@code _meta.ui.resourceUri} from {@link ContractsWidgetToolMetaProvider}.
 */
@Component
public class PassContractMcpResources {

    private static final String WIDGET_IFRAME_ID = "dcpass-contracts-widget";

    private final ObjectMapper objectMapper;

    public PassContractMcpResources(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * MCP Apps UI template: loads the real SPA route in a child frame and forwards {@code ui/notifications/tool-result}
     * from the sandbox parent to that frame (where the Angular widget listens).
     */
    @McpResource(
        uri = ContractsWidgetToolMetaProvider.WIDGET_RESOURCE_URI_DEFAULT,
        name = "PASS contracts widget",
        title = "PASS contracts widget",
        description = "Embeds the DCPASS contracts browser for ChatGPT (OpenAI Apps SDK).",
        mimeType = "text/html;profile=mcp-app",
        metaProvider = ContractsWidgetResourceMetaProvider.class
    )
    public String getContractsWidgetMcpTemplate() {
        String base = ContractsWidgetMcpConfig.getPublicBaseUrl();
        String iframeSrc = escapeHtmlAttribute(base + "/contracts-widget");
        return (
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\" />\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
            "<title>PASS contracts</title>\n" +
            "<style>html,body,iframe{margin:0;padding:0;border:0;width:100%;height:100%;}</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<iframe id=\"" +
            WIDGET_IFRAME_ID +
            "\" title=\"PASS contracts\" src=\"" +
            iframeSrc +
            "\"></iframe>\n" +
            "<script>\n" +
            "(function(){var iframe=document.getElementById('" +
            WIDGET_IFRAME_ID +
            "');window.addEventListener('message',function(event){if(event.source!==window.parent)return;if(!iframe||!iframe.contentWindow)return;iframe.contentWindow.postMessage(event.data,'*');},{passive:true});})();\n" +
            "</script>\n" +
            "</body>\n" +
            "</html>\n"
        );
    }

    private static String escapeHtmlAttribute(String raw) {
        return raw.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&#39;").replace("<", "&lt;").replace(">", "&gt;");
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
                    Map.of(
                        "dcpass://about",
                        "This server information",
                        ContractsWidgetToolMetaProvider.WIDGET_RESOURCE_URI_DEFAULT,
                        "ChatGPT widget template (text/html;profile=mcp-app)"
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
