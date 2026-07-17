import { expect, test } from './fixtures';
import { loginViaUI, USERS } from './helpers/auth';
import {
  createContractsViaApi,
  deleteContractViaApi,
  fillPassContractForm,
  type SeedContract,
} from './helpers/pass-contract';

test.describe('PASS Contracts', () => {
  let seeded: SeedContract[] = [];

  test.beforeAll(async ({ request }) => {
    seeded = await createContractsViaApi(request, 2);
  });

  test.afterAll(async ({ request }) => {
    for (const contract of seeded) {
      await deleteContractViaApi(request, contract.id);
    }
  });

  test('anonymous users can list and search contracts', async ({ page }) => {
    const marker = seeded[0];
    await page.goto('/pass-contract');
    await expect(page.getByTestId('PassContractHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(marker.contractNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(marker.title)).toBeVisible();
  });

  test('anonymous users can open contract details', async ({ page }) => {
    const marker = seeded[0];
    await page.goto('/pass-contract');
    await page.getByTestId('searchInput').fill(marker.contractNumber);
    await page.getByTestId('searchButton').click();
    await page.locator('tr', { hasText: marker.title }).getByTestId('entityDetailsButton').click();
    await expect(page.getByTestId('passContractDetailsHeading')).toBeVisible();
    await expect(page.getByText(marker.title)).toBeVisible();
  });

  test('advanced search validates inverted date ranges', async ({ page }) => {
    await page.goto('/pass-contract?awardDate.greaterThanOrEqual=2025-12-31&awardDate.lessThanOrEqual=2025-01-01');
    await expect(page.getByTestId('awardDateRangeValidationError')).toBeVisible();
  });

  test('authenticated user can create, edit, and delete a contract via UI', async ({ page }) => {
    const suffix = `${Date.now()}`;
    const title = `UI Created Contract ${suffix}`;
    const contractNumber = `UI-CN-${suffix}`;
    const updatedTitle = `UI Updated Contract ${suffix}`;

    await loginViaUI(page, USERS.admin.username, USERS.admin.password);
    await page.goto('/pass-contract');
    await page.getByTestId('entityCreateButton').click();
    await expect(page.getByTestId('PassContractCreateUpdateHeading')).toBeVisible();

    await fillPassContractForm(page, {
      title,
      contractNumber,
      supplier: `UI Supplier ${suffix}`,
      description: `UI description ${suffix}`,
      contractAmount: '250000',
    });
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('PassContractHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(contractNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(title)).toBeVisible();

    await page.locator('tr', { hasText: title }).getByTestId('entityEditButton').click();
    await page.getByTestId('title').fill(updatedTitle);
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('PassContractHeading')).toBeVisible();

    await page.getByTestId('searchInput').fill(contractNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(updatedTitle)).toBeVisible();

    await page.locator('tr', { hasText: updatedTitle }).getByTestId('entityDeleteButton').click();
    await page.getByTestId('entityConfirmDeleteButton').click();
    await page.getByTestId('searchInput').fill(contractNumber);
    await page.getByTestId('searchButton').click();
    await expect(page.getByText(updatedTitle)).toHaveCount(0);
  });

  test('create form requires authentication', async ({ page }) => {
    await page.goto('/pass-contract/new');
    await expect(page).toHaveURL(/\/login/);
  });
});
