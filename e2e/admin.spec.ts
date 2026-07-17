import { expect, test } from './fixtures';
import { loginViaUI, USERS } from './helpers/auth';

test.describe('Administration', () => {
  test.beforeEach(async ({ page }) => {
    await loginViaUI(page, USERS.admin.username, USERS.admin.password);
  });

  test('user management: list, create, view, edit, delete', async ({ page }) => {
    const login = `e2eu${Date.now()}`;
    const email = `${login}@localhost.local`;

    await page.goto('/admin/user-management');
    await expect(page.getByTestId('userManagementPageHeading')).toBeVisible();
    await expect(page.getByRole('cell', { name: 'admin', exact: true }).first()).toBeVisible();

    await page.getByTestId('entityCreateButton').click();
    await expect(page.getByTestId('userManagementCreateUpdateHeading')).toBeVisible();
    await page.locator('#field_login').fill(login);
    await page.locator('#field_firstName').fill('E2E');
    await page.locator('#field_lastName').fill('User');
    await page.locator('#field_email').fill(email);
    await page.locator('#field_authority').selectOption(['ROLE_USER']);
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('userManagementPageHeading')).toBeVisible();
    await expect(page.getByRole('cell', { name: login, exact: true })).toBeVisible();

    const row = page.locator('tr', { hasText: login });
    await row.getByTestId('entityDetailsButton').click();
    await expect(page.getByTestId('userManagementDetailsHeading')).toBeVisible();
    await expect(page.getByText(login, { exact: true }).first()).toBeVisible();
    await page.getByTestId('entityDetailsBackButton').click();

    await page.locator('tr', { hasText: login }).getByTestId('entityEditButton').click();
    await page.locator('#field_firstName').fill('E2E Updated');
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('userManagementPageHeading')).toBeVisible();

    await page.locator('tr', { hasText: login }).getByTestId('entityEditButton').click();
    await expect(page.locator('#field_firstName')).toHaveValue('E2E Updated');
    await page.getByTestId('entityCreateCancelButton').click();

    await page.locator('tr', { hasText: login }).getByTestId('entityDeleteButton').click();
    await expect(page.getByTestId('userManagementDeleteDialogHeading')).toBeVisible();
    await page.getByTestId('entityConfirmDeleteButton').click();
    await expect(page.getByRole('cell', { name: login, exact: true })).toHaveCount(0);
  });

  test('metrics page loads JVM and HTTP metrics', async ({ page }) => {
    await page.goto('/admin/metrics');
    await expect(page.getByTestId('metricsPageHeading')).toBeVisible();
    await expect(page.getByText(/JVM|Memory|Threads|HTTP/i).first()).toBeVisible();
  });

  test('health page shows components', async ({ page }) => {
    await page.goto('/admin/health');
    await expect(page.getByTestId('healthPageHeading')).toBeVisible();
    await expect(page.getByText('Database')).toBeVisible();
    await expect(page.getByText('UP').first()).toBeVisible();
  });

  test('configuration page lists property sources', async ({ page }) => {
    await page.goto('/admin/configuration');
    await expect(page.getByTestId('configurationPageHeading')).toBeVisible();
    await expect(page.locator('table').first()).toBeVisible();
  });

  test('logs page lists loggers and can change a level', async ({ page }) => {
    await page.goto('/admin/logs');
    await expect(page.getByTestId('logsPageHeading')).toBeVisible();
    await expect(page.getByText(/There are \d+ loggers/)).toBeVisible();

    const filter = page.locator('input.form-control').first();
    await filter.fill('io.argorand.poc.dcpass');
    const row = page.locator('tbody tr').filter({ hasText: 'io.argorand.poc.dcpass' }).first();
    await expect(row).toBeVisible();
    await row.getByRole('button', { name: 'WARN' }).click();
    await expect(row.getByRole('button', { name: 'WARN' })).toHaveClass(/btn-warning|btn-primary/);
    await row.getByRole('button', { name: 'INFO' }).click();
  });

  test('API docs (Swagger) frame is present', async ({ page }) => {
    await page.goto('/admin/docs');
    await expect(page.getByTestId('swagger-frame')).toBeVisible();
  });

  test('authority list and create/delete temporary authority', async ({ page }) => {
    const name = `ROLE_E2E_${Date.now()}`;
    await page.goto('/authority');
    await expect(page.getByTestId('AuthorityHeading')).toBeVisible();
    await expect(page.getByText('ROLE_ADMIN')).toBeVisible();

    await page.getByTestId('entityCreateButton').click();
    await page.getByTestId('name').fill(name);
    await page.getByTestId('entityCreateSaveButton').click();
    await expect(page.getByTestId('AuthorityHeading')).toBeVisible();
    await expect(page.getByRole('link', { name, exact: true })).toBeVisible();

    await page.locator('tr', { hasText: name }).getByTestId('entityDetailsButton').click();
    await expect(page.getByTestId('authorityDetailsHeading')).toBeVisible();
    await page.getByTestId('entityDetailsBackButton').click();

    await page.locator('tr', { hasText: name }).getByTestId('entityDeleteButton').click();
    await page.getByTestId('entityConfirmDeleteButton').click();
    await expect(page.getByRole('link', { name, exact: true })).toHaveCount(0);
  });
});
