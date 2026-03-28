package io.argorand.poc.dcpass.mcp;

import java.util.List;
import java.util.Map;
import org.springframework.ai.mcp.annotation.context.MetaProvider;

/**
 * OpenAI Apps SDK resource metadata: sandbox domain, CSP, and iframe allowlist for the contracts widget template.
 *
 * @see <a href="https://developers.openai.com/apps-sdk/build/mcp-server#step-1-register-a-component-template">Register a component template</a>
 */
public class ContractsWidgetResourceMetaProvider implements MetaProvider {

    private static final String OAISTATIC_PATTERN = "https://*.oaistatic.com";

    @Override
    public Map<String, Object> getMeta() {
        String base = ContractsWidgetMcpConfig.getPublicBaseUrl();
        return Map.of(
            "ui",
            Map.of(
                "domain",
                base,
                "csp",
                Map.of("frameDomains", List.of(base), "connectDomains", List.of(base), "resourceDomains", List.of(base, OAISTATIC_PATTERN))
            )
        );
    }
}
