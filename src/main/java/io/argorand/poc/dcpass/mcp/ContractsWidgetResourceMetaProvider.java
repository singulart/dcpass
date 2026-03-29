package io.argorand.poc.dcpass.mcp;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.ai.mcp.annotation.context.MetaProvider;

/**
 * OpenAI Apps SDK resource metadata: sandbox domain and CSP (connect/resource domains for loading the SPA from this server).
 *
 * @see <a href="https://developers.openai.com/apps-sdk/build/mcp-server#step-1-register-a-component-template">Register a component template</a>
 */
public class ContractsWidgetResourceMetaProvider implements MetaProvider {

    private static final List<String> OAISTATIC_PATTERNS = List.of(
        "https://*.oaistatic.com",
        "https://*.oaiusercontent.com",
        "https://*.web-sandbox.oaiusercontent.com",
        "https://persistent.oaistatic.com"
    );

    @Override
    public Map<String, Object> getMeta() {
        String base = ContractsWidgetMcpConfig.getPublicBaseUrl();
        List<String> cspDomains = Stream.concat(Stream.of(base), OAISTATIC_PATTERNS.stream()).toList();
        return Map.of("ui", Map.of("domain", base, "csp", Map.of("connectDomains", cspDomains, "resourceDomains", cspDomains)));
    }
}
