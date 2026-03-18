# ChatGPT App Integration

This document describes how to use the DCPASS MCP server with ChatGPT and render contract results in an embedded widget.

## Overview

- **Embedded widget**: When you connect a GPT to this MCP server and ask for contracts, the results are rendered in an iframe at `/contracts-widget` with pagination support.

## Widget URL

The widget that displays contract results in ChatGPT's iframe is served at:

```
https://<your-domain>/contracts-widget
```

For local development:

```
http://localhost:8080/contracts-widget
```

## Configuring Your GPT for Embedded Widget

To have ChatGPT render the contract table in an iframe when the AI calls `searchContractsInPASS`, you need to configure the tool with `_meta.ui.resourceUri`.

**Option 1: ChatGPT GPT configuration**

When creating or editing your GPT in ChatGPT:

1. Add the MCP server (your dcpass backend URL; for SSE transport use the URL ending with `/sse` or `/sse/`, e.g. `http://localhost:8080/sse` for dev).
2. If your GPT builder supports custom tool metadata, add:
   - `_meta.ui.resourceUri`: Your widget URL (e.g. `https://your-domain.com/contracts-widget`)

**Option 2: MCP server tool descriptor**

If your MCP implementation allows custom metadata on tools, configure the `searchContractsInPASS` tool with:

```json
{
  "_meta": {
    "ui": {
      "resourceUri": "https://your-domain.com/contracts-widget"
    }
  }
}
```

Spring AI MCP may require a custom configuration to add this. Check the [Spring AI MCP documentation](https://docs.spring.io/spring-ai/reference/api/mcp/) for tool metadata support.

## Data Flow

1. User asks ChatGPT: "Show me contracts over $1M"
2. ChatGPT calls `searchContractsInPASS` with `minimumContractAmount=1000000`
3. Your MCP server returns JSON in the tool result
4. ChatGPT loads your widget URL in an iframe and sends the result via `ui/notifications/tool-result` (postMessage)
5. The widget parses the JSON and renders the expandable table

## Iframe Embedding

For the widget to load in ChatGPT's iframe, your server must allow being framed. Ensure you do not set restrictive `X-Frame-Options` or `Content-Security-Policy` headers that block embedding by `*.openai.com` or `*.chatgpt.com`.

## Testing the Widget Standalone

You can test the widget at `/contracts-widget`. It will show "Waiting for contract results from ChatGPT…" until it receives data via postMessage. To test with mock data, open the browser console and run:

```javascript
// Legacy format (array) - no pagination
window.postMessage(
  {
    jsonrpc: '2.0',
    method: 'ui/notifications/tool-result',
    params: {
      content: [{ type: 'text', text: JSON.stringify([{ id: 1, title: 'Test Contract', description: 'Test description' }]) }],
    },
  },
  '*',
);

// Paginated format (from searchContractsInPASS)
window.postMessage(
  {
    jsonrpc: '2.0',
    method: 'ui/notifications/tool-result',
    params: {
      content: [
        {
          type: 'text',
          text: JSON.stringify({
            contracts: [{ id: 1, title: 'Test Contract', description: 'Test description' }],
            page: 0,
            totalItems: 1,
            size: 20,
            apiParams: { page: 0, size: 20, sort: 'lastModified,desc' },
          }),
        },
      ],
    },
  },
  '*',
);
```
