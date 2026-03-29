package io.argorand.poc.dcpass.mcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Builds the ChatGPT / MCP Apps widget HTML from the same JHipster {@code index.html} the SPA uses,
 * without an iframe: absolute {@code <base href>} so scripts and styles load from the public origin
 * when the document runs inside the host sandbox.
 */
@Component
public class ContractsWidgetSpaHtmlProvider {

    private static final Logger log = LoggerFactory.getLogger(ContractsWidgetSpaHtmlProvider.class);

    private static final Pattern BASE_TAG = Pattern.compile(
        "<base\\s+href\\s*=\\s*(['\"])([^'\"]*)\\1\\s*/?>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final String EMBED_FLAG_SCRIPT = "<script>window.__DCPASS_EMBED_CONTRACTS_WIDGET__=true</script>\n";

    /**
     * @return transformed SPA shell, or a minimal error page if loading fails
     */
    public String buildEmbeddedWidgetHtml() {
        try {
            String raw = loadRawIndexHtml();
            if (raw == null || raw.isBlank()) {
                throw new IllegalStateException("empty index.html");
            }
            return transform(raw, normalizedBase());
        } catch (Exception e) {
            log.warn("Failed to build embedded contracts widget HTML from SPA shell: {}", e.toString());
            return errorPage(normalizedBase(), e.getMessage());
        }
    }

    private static String normalizedBase() {
        String base = ContractsWidgetMcpConfig.getPublicBaseUrl().trim().replaceAll("/+$", "");
        return base.isEmpty() ? "https://localhost:8080" : base;
    }

    private String loadRawIndexHtml() throws IOException {
        ClassPathResource cp = new ClassPathResource("static/index.html");
        if (cp.exists()) {
            try (InputStream in = cp.getInputStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        String base = normalizedBase();
        log.info("static/index.html not on classpath; fetching SPA shell from {}", base);
        try {
            return httpFetchShell(base);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while fetching SPA shell: " + base + "/", e);
        }
    }

    private static String httpFetchShell(String base) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(base + "/")).timeout(Duration.ofSeconds(15)).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() >= 200 && res.statusCode() < 300 && res.body() != null && !res.body().isBlank()) {
            return res.body();
        }
        throw new IOException("HTTP " + res.statusCode() + " for SPA shell: " + base + "/");
    }

    static String transform(String html, String publicBaseNoTrailingSlash) {
        String baseWithSlash = publicBaseNoTrailingSlash + "/";
        String safeBase = escapeHtmlAttribute(baseWithSlash);
        String withBase;
        Matcher m = BASE_TAG.matcher(html);
        if (m.find()) {
            withBase = m.replaceFirst(Matcher.quoteReplacement("<base href=\"" + safeBase + "\" />"));
        } else {
            withBase = html.replaceFirst("(?i)(<head[^>]*>)", "$1\n<base href=\"" + safeBase + "\" />\n");
        }
        return withBase.replaceFirst("(?i)(<head[^>]*>)", "$1\n" + EMBED_FLAG_SCRIPT);
    }

    private static String escapeHtmlAttribute(String raw) {
        return raw.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&#39;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escapeBodyText(String raw) {
        return raw.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String errorPage(String base, String detail) {
        String msg = detail == null ? "unknown error" : escapeBodyText(detail);
        return (
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\"><head><meta charset=\"utf-8\"/><title>PASS contracts</title></head>\n" +
            "<body><p>Could not load the contracts widget shell. Check that the app is built and " +
            "<code>static/index.html</code> is on the classpath, or that <code>" +
            escapeHtmlAttribute(base) +
            "</code> is reachable.</p><p>" +
            msg +
            "</p></body></html>\n"
        );
    }
}
