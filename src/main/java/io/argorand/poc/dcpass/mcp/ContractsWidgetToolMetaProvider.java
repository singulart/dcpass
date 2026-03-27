package io.argorand.poc.dcpass.mcp;

import java.util.Map;
import org.springframework.ai.mcp.annotation.context.MetaProvider;

/**
 * Supplies OpenAI Apps / ChatGPT iframe UI metadata for {@code searchContractsInPASS}
 * ({@code _meta.ui.resourceUri}).
 *
 * <p>Set environment variable {@value #ENV_WIDGET_RESOURCE_URI} to override (e.g.
 * {@code ui://localhost:8080/contracts-widget} for MCP Inspector). If unset or blank, the default is
 * {@code ui://pass.argorand.io/contracts-widget}.
 */
public class ContractsWidgetToolMetaProvider implements MetaProvider {

    /** Environment variable that overrides the widget {@code resourceUri} when set and non-blank. */
    public static final String ENV_WIDGET_RESOURCE_URI = "DCPASS_MCP_WIDGET_RESOURCE_URI";

    private static final String DEFAULT_WIDGET_RESOURCE_URI = "ui://pass.argorand.io/contracts-widget";

    private static final String WIDGET_RESOURCE_URI = resolveWidgetResourceUri();

    private static String resolveWidgetResourceUri() {
        String fromEnv = System.getenv(ENV_WIDGET_RESOURCE_URI);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        return DEFAULT_WIDGET_RESOURCE_URI;
    }

    @Override
    public Map<String, Object> getMeta() {
        return Map.of("ui", Map.of("resourceUri", WIDGET_RESOURCE_URI));
    }
}
