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
