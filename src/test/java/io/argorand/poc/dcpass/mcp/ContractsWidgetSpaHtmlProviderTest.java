package io.argorand.poc.dcpass.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContractsWidgetSpaHtmlProviderTest {

    @Test
    void transform_stripsBaseRewritesRootRelativeAndInjectsEmbedFlag() {
        String html = "<html><head><base href=\"/\" /></head><body><script src=\"/main.js\"></script></body></html>";
        String out = ContractsWidgetSpaHtmlProvider.transform(html, "https://pass.example.com");
        assertThat(out).doesNotContain("<base");
        assertThat(out).contains("src=\"https://pass.example.com/main.js\"");
        assertThat(out).contains("window.__DCPASS_EMBED_CONTRACTS_WIDGET__=true");
    }

    @Test
    void transform_rewritesPathRelativeAssetsWhenNoBase() {
        String html =
            "<html><head><meta charset=\"utf-8\"/><link rel=\"stylesheet\" href=\"content/css/loading.css\"/></head><body></body></html>";
        String out = ContractsWidgetSpaHtmlProvider.transform(html, "https://app.example.org");
        assertThat(out).doesNotContain("<base");
        assertThat(out).contains("href=\"https://app.example.org/content/css/loading.css\"");
        assertThat(out).contains("__DCPASS_EMBED_CONTRACTS_WIDGET__");
    }

    @Test
    void transform_removesPwaManifestLink() {
        String html = "<html><head><link rel=\"manifest\" href=\"manifest.webapp\"/></head><body></body></html>";
        String out = ContractsWidgetSpaHtmlProvider.transform(html, "https://pass.example.com");
        assertThat(out).doesNotContain("rel=\"manifest\"");
        assertThat(out).doesNotContain("manifest.webapp");
    }
}
