package io.argorand.poc.dcpass.mcp;

import java.util.Map;
import org.springframework.ai.mcp.annotation.context.MetaProvider;

public class ContractsWidgetToolMetaProvider implements MetaProvider {

    public static final String WIDGET_RESOURCE_URI = "ui://widgets/pass-contracts.html";

    @Override
    public Map<String, Object> getMeta() {
        return Map.of("ui", Map.of("resourceUri", WIDGET_RESOURCE_URI));
    }
}
