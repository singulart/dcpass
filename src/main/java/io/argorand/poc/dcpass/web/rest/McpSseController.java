package io.argorand.poc.dcpass.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles HEAD requests for the MCP SSE endpoint.
 * The Spring AI MCP SSE transport only registers GET; clients (e.g. ChatGPT) that probe with HEAD get 404 otherwise.
 */
@RestController
public class McpSseController {

    @RequestMapping(value = { "/sse", "/sse/" }, method = RequestMethod.HEAD)
    public ResponseEntity<Void> sseHead() {
        return ResponseEntity.ok().build();
    }
}
