package io.argorand.poc.dcpass.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContractsWidgetSpaHtmlProviderTest {

    @Test
    void transform_rewritesBaseHrefAndInjectsEmbedFlag() {
        String html = "<html><head><base href=\"/\" /></head><body></body></html>";
        String out = ContractsWidgetSpaHtmlProvider.transform(html, "https://pass.example.com");
        assertThat(out).contains("<base href=\"https://pass.example.com/\" />");
        assertThat(out).contains("window.__DCPASS_EMBED_CONTRACTS_WIDGET__=true");
    }

    @Test
    void transform_insertsBaseWhenMissing() {
        String html = "<html><head><meta charset=\"utf-8\"/></head><body></body></html>";
        String out = ContractsWidgetSpaHtmlProvider.transform(html, "https://app.example.org");
        assertThat(out).contains("<base href=\"https://app.example.org/\" />");
        assertThat(out).contains("__DCPASS_EMBED_CONTRACTS_WIDGET__");
    }
}
