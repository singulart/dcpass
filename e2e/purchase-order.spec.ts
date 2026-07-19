import { expect, test } from './fixtures';
import { loginViaUI, USERS } from './helpers/auth';
import {
  createPurchaseOrdersViaApi,
  deletePurchaseOrderViaApi,
  fillPurchaseOrderForm,
  type SeedPurchaseOrder,
} from './helpers/purchase-order';

test.describe('Purchase Orders', () => {
  let seeded: SeedPurchaseOrder[] = [];

  test.beforeAll(async ({ request }) => {
    seeded = await createPurchaseOrdersViaApi(request, 2);
  });

  test.afterAll(async ({ request }) => {
    for (const purchaseOrder of seeded) {
      await deletePurchaseOrderViaApi(request, purchaseOrder.id);
    }
  });

  test('anonymous users can list and search purchase orders', async ({ page }) => {
    const marker = seeded[0];
    await page.goto(`/purchase-order?q=${encodeURIComponent(marker.poNumber)}`);
    await expect(page.getByTestId('PurchaseOrderHeading')).toBeVisible();
    await expect(page.getByText(marker.poTitle)).toBeVisible();
  });

  test('anonymous users can open purchase order details', async ({ page }) => {
    const marker = seeded[0];
    await page.goto(`/purchase-order?q=${encodeURIComponent(marker.poNumber)}`);
    await expect(page.getByText(marker.poTitle)).toBeVisible();
    await page.getByRole('link', { name: marker.poNumber }).click();
    await expect(page.getByTestId('purchaseOrderDetailsHeading')).toBeVisible();
    await expect(page.getByText(marker.poTitle)).toBeVisible();
  });

  test('advanced search validates inverted ordered date ranges', async ({ page }) => {
    await page.goto(
      '/purchase-order?orderedDate.greaterThanOrEqual=2025-12-31T00:00:00.000Z&orderedDate.lessThanOrEqual=2025-01-01T00:00:00.000Z',
    );
    await expect(page.getByTestId('orderedDateRangeValidationError')).toBeVisible();
  });

  test('authenticated user can create, edit, and delete a purchase order via UI', async ({ page }) => {
    const suffix = `${Date.now()}`;
    const poTitle = `UI Created Purchase Order ${suffix}`;
    const poNumber = `UI-PO-${suffix}`;
    const updatedTitle = `UI Updated Purchase Order ${suffix}`;

    await loginViaUI(page, USERS.admin.username, USERS.admin.password);
    await page.goto('/purchase-order');
    await page.getByTestId('entityCreateButton').click();
    await expect(page.getByTestId('PurchaseOrderCreateUpdateHeading')).toBeVisible();

    await fillPurchaseOrderForm(page, {
      poNumber,
      poTitle,
      supplier: `UI Supplier ${suffix}`,
      requisitionNumber: `UI-RQ-${suffix}`,
      poTotal: '25000',
    });
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('PurchaseOrderHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(poNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(poTitle)).toBeVisible();

    await page.locator('tr', { hasText: poTitle }).getByTestId('entityEditButton').click();
    await page.getByTestId('poTitle').fill(updatedTitle);
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('PurchaseOrderHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(poNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(updatedTitle)).toBeVisible();

    await page.locator('tr', { hasText: updatedTitle }).getByTestId('entityDeleteButton').click();
    await page.getByTestId('entityConfirmDeleteButton').click();
    await page.getByTestId('searchInput').fill(poNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(updatedTitle)).toHaveCount(0);
  });

  test('create form requires authentication', async ({ page }) => {
    await page.goto('/purchase-order/new');
    await expect(page).toHaveURL(/\/login/);
  });
});
