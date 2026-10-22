import type { Page } from '@playwright/test';
import { expect, test } from './fixtures';
import { loginViaUI, USERS } from './helpers/auth';
import {
  CONTRACT_STRING_FIELDS,
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

  for (const field of CONTRACT_STRING_FIELDS) {
    test.describe(`${field} filters`, () => {
      function fieldValue(contract: SeedContract): string {
        return contract[field];
      }

      function filterUrl(operator: string, ...values: string[]): string {
        const query = values.map(v => `filter[${field}.${operator}]=${encodeURIComponent(v)}`).join('&');
        return `/pass-contract?${query}`;
      }

      function row(page: Page, title: string) {
        return page.locator('tr[data-cy="entityTable"]', { hasText: title });
      }

      async function gotoFilter(page: Page, operator: string, values: string[], q?: string): Promise<void> {
        const listResponse = waitForPassContractsListResponse(page);
        const url = q ? `${filterUrl(operator, ...values)}&q=${encodeURIComponent(q)}` : filterUrl(operator, ...values);
        await page.goto(url);
        await listResponse;
      }

      test('equals', async ({ page }) => {
        await gotoFilter(page, 'equals', [fieldValue(seeded[0])]);
        await expect(row(page, seeded[0].title)).toBeVisible();
        await expect(row(page, seeded[1].title)).toHaveCount(0);
      });

      test('notEquals', async ({ page }) => {
        const excluded = seeded[0];
        const included = seeded[1];
        await gotoFilter(page, 'notEquals', [fieldValue(excluded)], excluded.contractNumber);
        await expect(row(page, excluded.title)).toHaveCount(0);

        await gotoFilter(page, 'notEquals', [fieldValue(excluded)], included.contractNumber);
        await expect(row(page, included.title)).toBeVisible();
      });

      test('in', async ({ page }) => {
        await gotoFilter(page, 'in', [fieldValue(seeded[0]), fieldValue(seeded[1])]);
        await expect(row(page, seeded[0].title)).toBeVisible();
        await expect(row(page, seeded[1].title)).toBeVisible();
      });

      test('notIn', async ({ page }) => {
        const excluded = seeded[0];
        const included = seeded[1];
        await gotoFilter(page, 'notIn', [fieldValue(excluded)], excluded.contractNumber);
        await expect(row(page, excluded.title)).toHaveCount(0);

        await gotoFilter(page, 'notIn', [fieldValue(excluded)], included.contractNumber);
        await expect(row(page, included.title)).toBeVisible();
      });

      test('contains', async ({ page }) => {
        await gotoFilter(page, 'contains', [fieldValue(seeded[0])]);
        await expect(row(page, seeded[0].title)).toBeVisible();
        await expect(row(page, seeded[1].title)).toHaveCount(0);
      });

      test('doesNotContain', async ({ page }) => {
        const excluded = seeded[0];
        const included = seeded[1];
        await gotoFilter(page, 'doesNotContain', [fieldValue(excluded)], excluded.contractNumber);
        await expect(row(page, excluded.title)).toHaveCount(0);

        await gotoFilter(page, 'doesNotContain', [fieldValue(excluded)], included.contractNumber);
        await expect(row(page, included.title)).toBeVisible();
      });

      test('specified=true includes seeded rows', async ({ page }) => {
        const marker = seeded[0];
        await gotoFilter(page, 'specified', ['true'], marker.contractNumber);
        await expect(row(page, marker.title)).toBeVisible();
      });

      test('specified=false excludes seeded rows', async ({ page }) => {
        const marker = seeded[0];
        await gotoFilter(page, 'specified', ['false'], marker.contractNumber);
        await expect(row(page, marker.title)).toHaveCount(0);
      });
    });
  }

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
