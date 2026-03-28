package io.argorand.poc.dcpass.mcp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Public origin of the SPA used inside the ChatGPT widget iframe ({@code /contracts-widget}).
 * Exposed as a static holder so {@link ContractsWidgetResourceMetaProvider} (not a Spring bean)
 * can build matching {@code _meta.ui.csp} entries.
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
