package io.argorand.poc.dcpass.mcp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Public origin of the SPA (same host that serves {@code /contracts-widget} and static assets).
 * Used for absolute {@code <base href>} in the MCP widget template and for {@code _meta.ui.csp} on the resource.
 */
@Component
public class ContractsWidgetMcpConfig {

    private static volatile String publicBaseUrl = "https://pass.argorand.io";

    public static String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    @Value("${dcpass.mcp.widget-public-base-url:https://pass.argorand.io}")
    public void setWidgetPublicBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            publicBaseUrl = "https://pass.argorand.io";
        } else {
            publicBaseUrl = url.trim().replaceAll("/+$", "");
        }
    }
}
