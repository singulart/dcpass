import { expect, type APIRequestContext, type Page } from '@playwright/test';
import { authHeaders, authenticateApi, USERS } from './auth';

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

export type SeedPurchaseOrder = {
  id: number;
  poNumber: string;
  poTitle: string;
  supplier: string;
  requisitionNumber: string;
  poTotal: number;
};

const runId = Date.now();

export function uniquePurchaseOrderPayload(index: number) {
  const suffix = `${runId}-${index}`;
  return {
    id: null as null,
    poNumber: `E2E-PO-${suffix}`,
    poTitle: `E2E Purchase Order ${suffix}`,
    supplier: `E2E Supplier ${suffix}`,
    requisitionNumber: `E2E-RQ-${suffix}`,
    agencyCode: 'E2E',
    agencyAcronym: 'E2E',
    agencyName: 'E2E Test Agency',
    status: 'Ordered',
    requester: 'E2E Requester',
    commodityCode: '1234567',
    commodityName: 'E2E Commodity',
    contractNumber: `E2E-CN-${suffix}`,
    poTotal: 10000 + index * 5000,
    fiscalYear: 2025,
    orderedDate: '2025-06-15T12:00:00Z',
    createDate: '2025-06-14T12:00:00Z',
  };
}

export async function createPurchaseOrdersViaApi(
  request: APIRequestContext,
  count = 2,
): Promise<SeedPurchaseOrder[]> {
  const token = await authenticateApi(request, USERS.admin.username, USERS.admin.password);
  const created: SeedPurchaseOrder[] = [];

  for (let i = 1; i <= count; i++) {
    const payload = uniquePurchaseOrderPayload(i);
    const response = await request.post(`${backendURL}/api/purchase-orders`, {
      headers: authHeaders(token),
      data: payload,
    });
    expect(response.ok(), await response.text()).toBeTruthy();
    const body = (await response.json()) as SeedPurchaseOrder;
    created.push({
      id: body.id,
      poNumber: payload.poNumber,
      poTitle: payload.poTitle,
      supplier: payload.supplier,
      requisitionNumber: payload.requisitionNumber,
      poTotal: payload.poTotal,
    });
  }

  return created;
}

export async function deletePurchaseOrderViaApi(request: APIRequestContext, id: number): Promise<void> {
  const token = await authenticateApi(request, USERS.admin.username, USERS.admin.password);
  const response = await request.delete(`${backendURL}/api/purchase-orders/${id}`, {
    headers: authHeaders(token),
  });
  expect([200, 204, 404]).toContain(response.status());
}

export async function fillPurchaseOrderForm(
  page: Page,
  values: {
    poNumber: string;
    poTitle: string;
    supplier: string;
    requisitionNumber: string;
    poTotal: string;
  },
): Promise<void> {
  await page.getByTestId('poNumber').fill(values.poNumber);
  await page.getByTestId('poTitle').fill(values.poTitle);
  await page.getByTestId('supplier').fill(values.supplier);
  await page.getByTestId('requisitionNumber').fill(values.requisitionNumber);
  await page.getByTestId('poTotal').fill(values.poTotal);
  await page.getByTestId('agencyCode').fill('E2E');
  await page.getByTestId('agencyAcronym').fill('E2E');
  await page.getByTestId('agencyName').fill('E2E Agency');
  await page.getByTestId('status').fill('Ordered');
}
