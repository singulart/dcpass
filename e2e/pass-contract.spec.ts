import type { Page } from '@playwright/test';
import { expect, test } from './fixtures';
import { loginViaUI, USERS } from './helpers/auth';
import {
  clickPassContractRowAction,
  createContractsViaApi,
  deleteContractViaApi,
  fillPassContractForm,
  searchPassContracts,
  waitForPassContractsListResponse,
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

    await searchPassContracts(page, marker.contractNumber, marker.title);
  });

  test.describe('commodity code filters', () => {
    function filterUrl(operator: string, ...values: string[]): string {
      const query = values.map(v => `filter[commodityCode.${operator}]=${encodeURIComponent(v)}`).join('&');
      return `/pass-contract?${query}`;
    }

    async function gotoFilter(page: Page, operator: string, ...values: string[]): Promise<void> {
      const listResponse = waitForPassContractsListResponse(page);
      await page.goto(filterUrl(operator, ...values));
      await listResponse;
    }

    test('equals', async ({ page }) => {
      await gotoFilter(page, 'equals', seeded[0].commodityCode);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toBeVisible();
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[1].title })).toHaveCount(0);
    });

    test('notEquals', async ({ page }) => {
      await gotoFilter(page, 'notEquals', seeded[0].commodityCode);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toHaveCount(0);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[1].title })).toBeVisible();
    });

    test('in', async ({ page }) => {
      await gotoFilter(page, 'in', seeded[0].commodityCode, seeded[1].commodityCode);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toBeVisible();
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[1].title })).toBeVisible();
    });

    test('notIn', async ({ page }) => {
      await gotoFilter(page, 'notIn', seeded[0].commodityCode);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toHaveCount(0);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[1].title })).toBeVisible();
    });

    test('contains', async ({ page }) => {
      const fragment = seeded[0].commodityCode.slice(-4);
      await gotoFilter(page, 'contains', fragment);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toBeVisible();
    });

    test('doesNotContain', async ({ page }) => {
      await gotoFilter(page, 'doesNotContain', seeded[0].commodityCode);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toHaveCount(0);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[1].title })).toBeVisible();
    });

    test('specified=true includes seeded rows', async ({ page }) => {
      await gotoFilter(page, 'specified', 'true');
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toBeVisible();
    });

    test('specified=false excludes seeded rows', async ({ page }) => {
      await gotoFilter(page, 'specified', 'false');
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[0].title })).toHaveCount(0);
      await expect(page.locator('tr[data-cy="entityTable"]', { hasText: seeded[1].title })).toHaveCount(0);
    });
  });

  test('anonymous users can open contract details', async ({ page }) => {
    const marker = seeded[0];
    const listResponse = waitForPassContractsListResponse(page);
    await page.goto(`/pass-contract?q=${encodeURIComponent(marker.contractNumber)}`);
    await listResponse;
    await expect(page.locator('tr[data-cy="entityTable"]', { hasText: marker.title })).toBeVisible();

    await clickPassContractRowAction(page, marker.title, 'entityDetailsButton');
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

    await searchPassContracts(page, contractNumber, title);
    await clickPassContractRowAction(page, title, 'entityEditButton');
    await page.getByTestId('title').fill(updatedTitle);
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('PassContractHeading')).toBeVisible();

    await searchPassContracts(page, contractNumber, updatedTitle);
    await clickPassContractRowAction(page, updatedTitle, 'entityDeleteButton');
    // Delete dialog calls load() directly; wait for that refresh (re-searching the same `q` would not GET).
    const listAfterDelete = waitForPassContractsListResponse(page);
    await page.getByTestId('entityConfirmDeleteButton').click();
    await listAfterDelete;
    await expect(page.getByText(updatedTitle)).toHaveCount(0);
  });

  test('create form requires authentication', async ({ page }) => {
    await page.goto('/pass-contract/new');
    await expect(page).toHaveURL(/\/login/);
  });
});
