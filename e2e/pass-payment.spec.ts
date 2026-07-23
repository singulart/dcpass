import { expect, test } from './fixtures';
import { loginViaUI, USERS } from './helpers/auth';
import {
  createPassPaymentsViaApi,
  deletePassPaymentViaApi,
  fillPassPaymentForm,
  type SeedPassPayment,
} from './helpers/pass-payment';

test.describe('PASS Payments', () => {
  let seeded: SeedPassPayment[] = [];

  test.beforeAll(async ({ request }) => {
    seeded = await createPassPaymentsViaApi(request, 2);
  });

  test.afterAll(async ({ request }) => {
    for (const payment of seeded) {
      await deletePassPaymentViaApi(request, payment.id);
    }
  });

  test('anonymous users can list and search payments', async ({ page }) => {
    const marker = seeded[0];
    await page.goto(`/pass-payment?q=${encodeURIComponent(marker.paymentNumber)}`);
    await expect(page.getByTestId('PassPaymentHeading')).toBeVisible();
    await expect(page.getByText(marker.supplierName)).toBeVisible();
    await expect(page.getByRole('link', { name: marker.paymentNumber })).toBeVisible();
  });

  test('anonymous users can search payments by purchase order number', async ({ page }) => {
    const marker = seeded[0];
    await page.goto('/pass-payment');
    await expect(page.getByTestId('PassPaymentHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(marker.poNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(marker.supplierName)).toBeVisible();
    await expect(page.getByText(marker.poNumber)).toBeVisible();
  });

  test('anonymous users can open payment details', async ({ page }) => {
    const marker = seeded[0];
    await page.goto(`/pass-payment?q=${encodeURIComponent(marker.paymentNumber)}`);
    await expect(page.getByText(marker.supplierName)).toBeVisible();
    await page.getByRole('link', { name: marker.paymentNumber }).click();
    await expect(page.getByTestId('passPaymentDetailsHeading')).toBeVisible();
    await expect(page.getByText(marker.paymentNumber)).toBeVisible();
    await expect(page.getByText(marker.supplierName)).toBeVisible();
    await expect(page.getByText(marker.poNumber)).toBeVisible();
    await expect(page.getByText(marker.contractNumber)).toBeVisible();
    await expect(page.getByText(String(marker.paymentAmount))).toBeVisible();
  });

  test('anonymous users can open payment details from the view action', async ({ page }) => {
    const marker = seeded[1];
    await page.goto(`/pass-payment?q=${encodeURIComponent(marker.paymentNumber)}`);
    await expect(page.getByText(marker.supplierName)).toBeVisible();
    await page.locator('tr', { hasText: marker.supplierName }).getByTestId('entityDetailsButton').click();
    await expect(page.getByTestId('passPaymentDetailsHeading')).toBeVisible();
    await expect(page.getByText(marker.invoiceNumber)).toBeVisible();
    await expect(page.getByText(marker.voucherNumber)).toBeVisible();
  });

  test('advanced search validates inverted payment date ranges', async ({ page }) => {
    await page.goto(
      '/pass-payment?paymentDate.greaterThanOrEqual=2025-12-31T00:00:00.000Z&paymentDate.lessThanOrEqual=2025-01-01T00:00:00.000Z',
    );
    await expect(page.getByTestId('paymentDateRangeValidationError')).toBeVisible();
  });

  test('authenticated user can create, edit, and delete a payment via UI', async ({ page }) => {
    const suffix = `${Date.now()}`;
    const paymentNumber = `UI-PAY-${suffix}`;
    const supplierName = `UI Payment Supplier ${suffix}`;
    const updatedSupplierName = `UI Updated Payment Supplier ${suffix}`;
    const invoiceNumber = `UI-INV-${suffix}`;
    const poNumber = `UI-PO-${suffix}`;
    const voucherNumber = `UI-VB-${suffix}`;
    const contractNumber = `UI-CN-${suffix}`;

    await loginViaUI(page, USERS.admin.username, USERS.admin.password);
    await page.goto('/pass-payment');
    await page.getByTestId('entityCreateButton').click();
    await expect(page.getByTestId('PassPaymentCreateUpdateHeading')).toBeVisible();

    await fillPassPaymentForm(page, {
      paymentNumber,
      supplierName,
      invoiceNumber,
      poNumber,
      voucherNumber,
      contractNumber,
      paymentAmount: '2750',
    });
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('PassPaymentHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(paymentNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(supplierName)).toBeVisible();

    await page.locator('tr', { hasText: supplierName }).getByTestId('entityEditButton').click();
    await page.getByTestId('supplierName').fill(updatedSupplierName);
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('PassPaymentHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(paymentNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(updatedSupplierName)).toBeVisible();

    await page.locator('tr', { hasText: updatedSupplierName }).getByTestId('entityDeleteButton').click();
    await page.getByTestId('entityConfirmDeleteButton').click();
    await page.getByTestId('searchInput').fill(paymentNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(updatedSupplierName)).toHaveCount(0);
  });

  test('create form requires authentication', async ({ page }) => {
    await page.goto('/pass-payment/new');
    await expect(page).toHaveURL(/\/login/);
  });

  test('edit form requires authentication', async ({ page }) => {
    const marker = seeded[0];
    await page.goto(`/pass-payment/${marker.id}/edit`);
    await expect(page).toHaveURL(/\/login/);
  });
});
