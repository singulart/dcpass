package io.argorand.poc.dcpass.security;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Matches the SPA route used inside the ChatGPT widget iframe and the internal forward to {@code index.html}.
 * Used to omit {@code X-Frame-Options: SAMEORIGIN} so OpenAI's sandbox can embed the page.
 */
public final class ContractsWidgetEmbedRequestMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest request) {
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }
        String path = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
        if (path.isEmpty()) {
            path = "/";
        }

        if ("/contracts-widget".equals(path) || path.startsWith("/contracts-widget/")) {
            return true;
        }

        if ("/index.html".equals(path)) {
            Object forwardUri = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
            if (forwardUri instanceof String s) {
                String fp = s.startsWith(contextPath) ? s.substring(contextPath.length()) : s;
                return "/contracts-widget".equals(fp) || fp.startsWith("/contracts-widget/");
            }
        }

        return false;
    }
}
