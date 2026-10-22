import { expect, type APIRequestContext, type Page } from '@playwright/test';
import { authHeaders, authenticateApi, USERS } from './auth';

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

/** Every JHipster StringFilter property on a PASS contract. */
export const CONTRACT_STRING_FIELDS = [
  'procurementMethodDescription',
  'agencyAcronym',
  'agencyName',
  'agency',
  'contractNumber',
  'contractStatus',
  'title',
  'contractingOfficer',
  'marketType',
  'commodityCode',
  'commodityDescription',
  'currentOptionPeriod',
  'supplier',
  'description',
  'contractTypeDescription',
  'contractingOfficerEmail',
  'vendorAddress',
  'vendorCity',
  'vendorState',
  'vendorZip',
  'publishedVersionId',
  'documentVersion',
  'contractingSplst',
  'contractingSplstEmail',
  'source',
  'contractDetailsLink',
  'contractAdministratorName',
  'contractAdministratorEmail',
  'contractAdministratorPhone',
  'contractOfficerPhone',
  'cwInternalId',
  'corporatePhone',
  'corporateEmailAddress',
] as const;

export type ContractStringField = (typeof CONTRACT_STRING_FIELDS)[number];

export type SeedContract = {
  id: number;
  contractAmount: number;
  awardDate: string;
  startDate: string;
  endDate: string;
} & { [K in ContractStringField]: string };

const runId = Date.now();

function uniqueContractStrings(index: number): { [K in ContractStringField]: string } {
  const suffix = `${runId}-${index}`;
  const values = Object.fromEntries(CONTRACT_STRING_FIELDS.map(field => [field, `${field}${index}X${runId}`])) as {
    [K in ContractStringField]: string;
  };
  // Keep the identifiers the rest of the suite searches for, and keep them from being substrings of each other.
  values.title = `E2E PASS Contract ${suffix}`;
  values.contractNumber = `E2E-CN-${suffix}`;
  values.supplier = `E2E Supplier ${suffix}`;
  values.description = `E2E searchable description marker ${suffix}`;
  values.commodityCode = `${8000000 + (runId % 100000) + index}`;
  values.agencyAcronym = `AA${index}X${runId}`;
  values.agencyName = `E2E Agency ${index}X${runId}`;
  return values;
}

export function uniqueContractPayload(index: number) {
  return {
    id: null as null,
    ...uniqueContractStrings(index),
    contractAmount: 100000 + index * 50000,
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
    const body = (await response.json()) as { id: number };
    const strings = Object.fromEntries(CONTRACT_STRING_FIELDS.map(field => [field, payload[field]])) as {
      [K in ContractStringField]: string;
    };
    created.push({
      ...strings,
      id: body.id,
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

function searchQueryFromUrl(url: string): string {
  try {
    return new URL(url).searchParams.get('q') ?? '';
  } catch {
    return '';
  }
}

/** Search the contracts list and wait until the filtered row is stable. */
export async function searchPassContracts(page: Page, query: string, expectedRowText: string): Promise<void> {
  const trimmed = query.trim();
  await page.getByTestId('searchInput').fill(trimmed);

  // Re-searching the same `q` does not change Angular query params, so handleNavigation
  // skips a new GET (common after edit save via history.back() to a filtered list).
  if (searchQueryFromUrl(page.url()) !== trimmed) {
    const listResponse = waitForPassContractsListResponse(page);
    await page.getByTestId('searchButton').click();
    await listResponse;
  }

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
