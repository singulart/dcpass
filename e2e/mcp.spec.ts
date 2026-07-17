import { expect, test } from './fixtures';
import { createContractsViaApi, deleteContractViaApi, type SeedContract } from './helpers/pass-contract';
import { McpSseClient } from './helpers/mcp-client';

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

type ToolCallResult = {
  isError?: boolean;
  content?: Array<{ type: string; text?: string }>;
  structuredContent?: {
    contracts: Array<{ contractNumber?: string; title?: string; contractAmount?: number }>;
    totalItems: number;
    page?: number;
    apiParams?: Record<string, unknown>;
  };
};

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
    for (const tool of tools) {
      expect(tool.name).toBeTruthy();
      expect(tool.description ?? '').toMatch(/PASS|contract/i);
    }
  });

  test('searchContractsInPASS accepts every parameter together', async () => {
    const marker = seeded[0];
    const response = await client.callTool('searchContractsInPASS', {
      q: marker.contractNumber,
      awardDateFrom: '2025-06-01',
      awardDateTo: '2025-06-30',
      startDateFrom: '2025-06-15',
      startDateTo: '2025-07-15',
      endDateFrom: '2026-06-01',
      endDateTo: '2026-12-31',
      minimumContractAmount: String(marker.contractAmount - 1),
      maximumContractAmount: String(marker.contractAmount + 1),
      page: 1,
    });

    expect(response.error).toBeUndefined();
    const result = response.result as ToolCallResult;
    expect(result.isError).not.toBe(true);
    expect(result.structuredContent).toBeTruthy();
    expect(result.structuredContent!.totalItems).toBeGreaterThan(0);
    expect(result.structuredContent!.contracts.some(c => c.contractNumber === marker.contractNumber)).toBeTruthy();

    const apiParams = result.structuredContent!.apiParams ?? {};
    expect(apiParams.q).toBe(marker.contractNumber);
    expect(apiParams['awardDate.greaterThanOrEqual']).toBe('2025-06-01');
    expect(apiParams['awardDate.lessThanOrEqual']).toBe('2025-06-30');
    expect(apiParams['startDate.greaterThanOrEqual']).toBe('2025-06-15');
    expect(apiParams['startDate.lessThanOrEqual']).toBe('2025-07-15');
    expect(apiParams['endDate.greaterThanOrEqual']).toBe('2026-06-01');
    expect(apiParams['endDate.lessThanOrEqual']).toBe('2026-12-31');
    expect(apiParams['contractAmount.greaterThanOrEqual']).toBe(String(marker.contractAmount - 1));
    expect(apiParams['contractAmount.lessThanOrEqual']).toBe(String(marker.contractAmount + 1));
    expect(apiParams.page).toBe(0); // normalized from 1-based page=1
  });

  test('searchContractsInPASS treats page 0 as first page', async () => {
    const response = await client.callTool('searchContractsInPASS', {
      q: seeded[0].contractNumber,
      page: 0,
    });
    expect(response.error).toBeUndefined();
    const result = response.result as ToolCallResult;
    expect(result.isError).not.toBe(true);
    expect(result.structuredContent?.page).toBe(0);
    expect(result.structuredContent?.contracts.some(c => c.contractNumber === seeded[0].contractNumber)).toBeTruthy();
  });

  test('searchContractsInPASS returns tool-level errors for invalid inputs', async () => {
    const invalidDate = await client.callTool('searchContractsInPASS', {
      awardDateFrom: 'not-a-date',
      startDateTo: 'also-bad',
      endDateFrom: '2025-13-40',
    });
    const invalidDateResult = invalidDate.result as ToolCallResult;
    expect(invalidDateResult.isError).toBe(true);
    expect(invalidDateResult.content?.[0]?.text ?? '').toMatch(/Invalid input|date/i);

    const invalidAmount = await client.callTool('searchContractsInPASS', {
      minimumContractAmount: 'not-a-number',
      maximumContractAmount: '-100',
    });
    const invalidAmountResult = invalidAmount.result as ToolCallResult;
    expect(invalidAmountResult.isError).toBe(true);
    expect(invalidAmountResult.content?.[0]?.text ?? '').toMatch(/Invalid input|number|negative/i);

    const invertedAmounts = await client.callTool('searchContractsInPASS', {
      minimumContractAmount: '500000',
      maximumContractAmount: '1000',
    });
    const invertedResult = invertedAmounts.result as ToolCallResult;
    expect(invertedResult.isError).toBe(true);
    expect(invertedResult.content?.[0]?.text ?? '').toMatch(/minimumContractAmount|greater than/i);

    const badPage = await client.callTool('searchContractsInPASS', { page: -1 });
    const badPageResult = badPage.result as ToolCallResult;
    expect(badPageResult.isError).toBe(true);
    expect(badPageResult.content?.[0]?.text ?? '').toMatch(/page/i);

    const longQuery = await client.callTool('searchContractsInPASS', {
      q: 'x'.repeat(501),
    });
    const longQueryResult = longQuery.result as ToolCallResult;
    expect(longQueryResult.isError).toBe(true);
    expect(longQueryResult.content?.[0]?.text ?? '').toMatch(/at most 500|Search phrase/i);
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
