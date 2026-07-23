import { expect, type APIRequestContext, type Page } from '@playwright/test';
import { authHeaders, authenticateApi, USERS } from './auth';

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

export type SeedContract = {
  id: number;
  title: string;
  contractNumber: string;
  supplier: string;
  description: string;
  contractAmount: number;
  awardDate: string;
  startDate: string;
  endDate: string;
};

const runId = Date.now();

export function uniqueContractPayload(index: number) {
  const suffix = `${runId}-${index}`;
  return {
    id: null as null,
    title: `E2E PASS Contract ${suffix}`,
    contractNumber: `E2E-CN-${suffix}`,
    supplier: `E2E Supplier ${suffix}`,
    description: `E2E searchable description marker ${suffix}`,
    agencyAcronym: 'E2E',
    agencyName: 'E2E Test Agency',
    contractAmount: 100000 + index * 50000,
    contractStatus: 'Active',
    awardDate: '2025-06-15',
    startDate: '2025-07-01',
    endDate: '2026-06-30',
    fiscalYear: 2025,
  };
}

export async function createContractsViaApi(
  request: APIRequestContext,
  count = 2,
): Promise<SeedContract[]> {
  const token = await authenticateApi(request, USERS.admin.username, USERS.admin.password);
  const created: SeedContract[] = [];

  for (let i = 1; i <= count; i++) {
    const payload = uniqueContractPayload(i);
    const response = await request.post(`${backendURL}/api/pass-contracts`, {
      headers: authHeaders(token),
      data: payload,
    });
    expect(response.ok(), await response.text()).toBeTruthy();
    const body = (await response.json()) as SeedContract;
    created.push({
      id: body.id,
      title: payload.title,
      contractNumber: payload.contractNumber,
      supplier: payload.supplier,
      description: payload.description,
      contractAmount: payload.contractAmount,
      awardDate: payload.awardDate,
      startDate: payload.startDate,
      endDate: payload.endDate,
    });
  }

  return created;
}

export async function deleteContractViaApi(request: APIRequestContext, id: number): Promise<void> {
  const token = await authenticateApi(request, USERS.admin.username, USERS.admin.password);
  const response = await request.delete(`${backendURL}/api/pass-contracts/${id}`, {
    headers: authHeaders(token),
  });
  expect([200, 204, 404]).toContain(response.status());
}

export async function fillPassContractForm(
  page: Page,
  values: {
    title: string;
    contractNumber: string;
    supplier: string;
    description: string;
    contractAmount: string;
  },
): Promise<void> {
  await page.getByTestId('title').fill(values.title);
  await page.getByTestId('contractNumber').fill(values.contractNumber);
  await page.getByTestId('supplier').fill(values.supplier);
  await page.getByTestId('description').fill(values.description);
  await page.getByTestId('contractAmount').fill(values.contractAmount);
  await page.getByTestId('agencyAcronym').fill('E2E');
  await page.getByTestId('agencyName').fill('E2E Agency');
  await page.getByTestId('contractStatus').fill('Active');
}

/**
 * Waits for a contracts list API response. The large contracts table re-renders after search,
 * so callers should wait for this before clicking row actions.
 */
export function waitForPassContractsListResponse(page: Page) {
  return page.waitForResponse(
    response =>
      response.request().method() === 'GET' &&
      response.url().includes('/api/pass-contracts') &&
      !response.url().includes('/payment-summary/') &&
      response.ok(),
  );
}

/** Search the contracts list and wait until the filtered row is stable. */
export async function searchPassContracts(page: Page, query: string, expectedRowText: string): Promise<void> {
  await page.getByTestId('searchInput').fill(query);
  const listResponse = waitForPassContractsListResponse(page);
  await page.getByTestId('searchButton').click();
  await listResponse;
  await expect(page).toHaveURL(new RegExp(`[?&]q=`));
  await expect(page.locator('tr[data-cy="entityTable"]', { hasText: expectedRowText })).toBeVisible();
}

/**
 * Clicks a row action after the contracts table finishes re-rendering.
 * Retries because Angular replaces row DOM nodes when search/list responses arrive.
 */
export async function clickPassContractRowAction(page: Page, rowText: string, actionTestId: string): Promise<void> {
  await expect(async () => {
    const action = page.locator('tr[data-cy="entityTable"]', { hasText: rowText }).getByTestId(actionTestId);
    await expect(action).toBeVisible();
    await action.click({ timeout: 3000 });
  }).toPass();
}
