import { expect, type APIRequestContext, type Page } from '@playwright/test';
import { authHeaders, authenticateApi, USERS } from './auth';

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

export type SeedPassPayment = {
  id: number;
  paymentNumber: string;
  supplierName: string;
  invoiceNumber: string;
  poNumber: string;
  voucherNumber: string;
  contractNumber: string;
  paymentAmount: number;
};

const runId = Date.now();

export function uniquePassPaymentPayload(index: number) {
  const suffix = `${runId}-${index}`;
  return {
    id: null as null,
    paymentNumber: `E2E-PAY-${suffix}`,
    supplierName: `E2E Payment Supplier ${suffix}`,
    invoiceNumber: `E2E-INV-${suffix}`,
    poNumber: `E2E-PO-${suffix}`,
    voucherNumber: `E2E-VB-${suffix}`,
    contractNumber: `E2E-CN-${suffix}`,
    agencyCode: 'E2E',
    agencyAcronym: 'E2E',
    agencyName: 'E2E Test Agency',
    paymentAmount: 1500 + index * 250,
    fiscalYear: 2025,
    transactionCode: '242',
    paymentType: 'CHECK',
    paymentDate: '2025-06-15T12:00:00Z',
    invoiceDate: '2025-06-10T12:00:00Z',
    estPaymentDate: '2025-06-14T12:00:00Z',
  };
}

export async function createPassPaymentsViaApi(request: APIRequestContext, count = 2): Promise<SeedPassPayment[]> {
  const token = await authenticateApi(request, USERS.admin.username, USERS.admin.password);
  const created: SeedPassPayment[] = [];

  for (let i = 1; i <= count; i++) {
    const payload = uniquePassPaymentPayload(i);
    const response = await request.post(`${backendURL}/api/pass-payments`, {
      headers: authHeaders(token),
      data: payload,
    });
    expect(response.ok(), await response.text()).toBeTruthy();
    const body = (await response.json()) as SeedPassPayment;
    created.push({
      id: body.id,
      paymentNumber: payload.paymentNumber,
      supplierName: payload.supplierName,
      invoiceNumber: payload.invoiceNumber,
      poNumber: payload.poNumber,
      voucherNumber: payload.voucherNumber,
      contractNumber: payload.contractNumber,
      paymentAmount: payload.paymentAmount,
    });
  }

  return created;
}

export async function deletePassPaymentViaApi(request: APIRequestContext, id: number): Promise<void> {
  const token = await authenticateApi(request, USERS.admin.username, USERS.admin.password);
  const response = await request.delete(`${backendURL}/api/pass-payments/${id}`, {
    headers: authHeaders(token),
  });
  expect([200, 204, 404]).toContain(response.status());
}

export async function fillPassPaymentForm(
  page: Page,
  values: {
    paymentNumber: string;
    supplierName: string;
    invoiceNumber: string;
    poNumber: string;
    voucherNumber: string;
    contractNumber: string;
    paymentAmount: string;
  },
): Promise<void> {
  await page.getByTestId('paymentNumber').fill(values.paymentNumber);
  await page.getByTestId('supplierName').fill(values.supplierName);
  await page.getByTestId('invoiceNumber').fill(values.invoiceNumber);
  await page.getByTestId('poNumber').fill(values.poNumber);
  await page.getByTestId('voucherNumber').fill(values.voucherNumber);
  await page.getByTestId('contractNumber').fill(values.contractNumber);
  await page.getByTestId('paymentAmount').fill(values.paymentAmount);
  await page.getByTestId('agencyCode').fill('E2E');
  await page.getByTestId('agencyAcronym').fill('E2E');
  await page.getByTestId('agencyName').fill('E2E Agency');
  await page.getByTestId('paymentType').fill('CHECK');
  await page.getByTestId('fiscalYear').fill('2025');
}
