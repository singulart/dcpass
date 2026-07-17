import { expect, test } from './fixtures';
import { createContractsViaApi, deleteContractViaApi, type SeedContract } from './helpers/pass-contract';
import { McpSseClient } from './helpers/mcp-client';

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

test.describe('MCP server', () => {
  let seeded: SeedContract[] = [];
  let client: McpSseClient;

  test.beforeAll(async ({ request }) => {
    seeded = await createContractsViaApi(request, 2);
    client = new McpSseClient(backendURL);
    await client.connect();
  });

  test.afterAll(async ({ request }) => {
    await client?.close();
    for (const contract of seeded) {
      await deleteContractViaApi(request, contract.id);
    }
  });

  test('lists tools including searchContractsInPASS', async () => {
    const tools = await client.listTools();
    const names = tools.map(t => t.name);
    expect(names).toContain('searchContractsInPASS');
    expect(tools).toHaveLength(names.length);
    // Every declared tool should be invocable — currently one tool.
    for (const tool of tools) {
      expect(tool.name).toBeTruthy();
      expect(tool.description ?? '').toMatch(/PASS|contract/i);
    }
  });

  test('invokes searchContractsInPASS for every listed tool with filters', async () => {
    const tools = await client.listTools();
    expect(tools.length).toBeGreaterThan(0);

    for (const tool of tools) {
      if (tool.name === 'searchContractsInPASS') {
        const response = await client.callTool(tool.name, {
          q: seeded[0].contractNumber,
          page: 1,
        });
        expect(response.error).toBeUndefined();
        const result = response.result as {
          isError?: boolean;
          content?: Array<{ type: string; text?: string }>;
          structuredContent?: {
            contracts: Array<{ contractNumber?: string; title?: string }>;
            totalItems: number;
          };
        };
        expect(result.isError).not.toBe(true);
        const structured = result.structuredContent;
        expect(structured).toBeTruthy();
        expect(structured!.totalItems).toBeGreaterThan(0);
        expect(structured!.contracts.some(c => c.contractNumber === seeded[0].contractNumber)).toBeTruthy();

        const amountResponse = await client.callTool(tool.name, {
          minimumContractAmount: String(seeded[0].contractAmount - 1),
          maximumContractAmount: String(seeded[0].contractAmount + 1),
          q: seeded[0].title.split(' ').slice(-1)[0],
        });
        expect(amountResponse.error).toBeUndefined();

        const invalid = await client.callTool(tool.name, {
          awardDateFrom: 'not-a-date',
        });
        const invalidResult = invalid.result as { isError?: boolean; content?: Array<{ text?: string }> };
        expect(invalidResult.isError).toBe(true);
        expect(invalidResult.content?.[0]?.text ?? '').toMatch(/Invalid input|date/i);
      } else {
        // Future tools: call with empty args and assert no transport-level error.
        const response = await client.callTool(tool.name, {});
        expect(response.error).toBeUndefined();
      }
    }
  });

  test('lists and reads MCP resources', async () => {
    const resources = await client.listResources();
    const uris = resources.map(r => r.uri);
    expect(uris).toContain('dcpass://about');
    expect(uris).toContain('ui://widgets/pass-contracts.html');

    const about = await client.readResource('dcpass://about');
    expect(about.error).toBeUndefined();
    const aboutResult = about.result as { contents: Array<{ text?: string; uri?: string }> };
    const aboutText = aboutResult.contents.map(c => c.text ?? '').join('\n');
    expect(aboutText).toMatch(/dcpass-mcp|searchContractsInPASS/);

    const widget = await client.readResource('ui://widgets/pass-contracts.html');
    expect(widget.error).toBeUndefined();
    const widgetResult = widget.result as { contents: Array<{ text?: string; mimeType?: string }> };
    const html = widgetResult.contents.map(c => c.text ?? '').join('\n');
    expect(html).toMatch(/html|__DCPASS_EMBED_CONTRACTS_WIDGET__|contracts-widget/i);
  });

  test('SSE endpoint accepts HEAD for health probes', async ({ request }) => {
    const response = await request.fetch(`${backendURL}/sse`, { method: 'HEAD' });
    expect(response.status()).toBeLessThan(500);
  });
});
