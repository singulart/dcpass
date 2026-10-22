import { expect, type APIRequestContext, type Page } from '@playwright/test';
import { authHeaders, authenticateApi, USERS } from './auth';

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

/** Every JHipster StringFilter property on a PASS payment. */
export const PAYMENT_STRING_FIELDS = [
  'agencyCode',
  'agencyAcronym',
  'agencyName',
  'contractNumber',
  'supplierName',
  'invoiceNumber',
  'poNumber',
  'voucherNumber',
  'transactionCode',
  'paymentType',
  'paymentNumber',
] as const;

export type PaymentStringField = (typeof PAYMENT_STRING_FIELDS)[number];

export type SeedPassPayment = {
  id: number;
  paymentAmount: number;
} & { [K in PaymentStringField]: string };

const runId = Date.now();

function uniquePassPaymentStrings(index: number): { [K in PaymentStringField]: string } {
  const suffix = `${runId}-${index}`;
  const values = Object.fromEntries(PAYMENT_STRING_FIELDS.map(field => [field, `${field}${index}X${runId}`])) as {
    [K in PaymentStringField]: string;
  };
  // Keep the identifiers the rest of the suite searches for, and keep them from being substrings of each other.
  values.paymentNumber = `E2E-PAY-${suffix}`;
  values.supplierName = `E2E Payment Supplier ${suffix}`;
  values.invoiceNumber = `E2E-INV-${suffix}`;
  values.poNumber = `E2E-PO-${suffix}`;
  values.voucherNumber = `E2E-VB-${suffix}`;
  values.contractNumber = `E2E-CN-${suffix}`;
  values.agencyCode = `AC${index}X${runId}`;
  values.agencyAcronym = `AA${index}X${runId}`;
  values.agencyName = `E2E Agency ${index}X${runId}`;
  return values;
}

export function uniquePassPaymentPayload(index: number) {
  return {
    id: null as null,
    ...uniquePassPaymentStrings(index),
    paymentAmount: 1500 + index * 250,
    fiscalYear: 2025,
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
    const body = (await response.json()) as { id: number };
    const strings = Object.fromEntries(PAYMENT_STRING_FIELDS.map(field => [field, payload[field]])) as {
      [K in PaymentStringField]: string;
    };
    created.push({
      ...strings,
      id: body.id,
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
