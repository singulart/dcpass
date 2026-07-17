import { expect, test } from './fixtures';
import { loginViaUI, logoutViaUI, USERS } from './helpers/auth';

test.describe('Authentication', () => {
  test('home page shows sign-in prompt when anonymous', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('heading', { name: 'PASS Contracts' })).toBeVisible();
    await expect(page.locator('.alert-warning a.alert-link')).toHaveText('sign in');
  });

  test('rejects invalid credentials', async ({ page }) => {
    await page.goto('/login');
    await page.getByTestId('username').fill('admin');
    await page.getByTestId('password').fill('wrong-password');
    await page.getByTestId('submit').click();
    await expect(page.getByTestId('loginError')).toBeVisible();
  });

  test('admin can sign in and sign out', async ({ page }) => {
    await loginViaUI(page, USERS.admin.username, USERS.admin.password);
    await page.goto('/');
    await expect(page.locator('#home-logged-message')).toContainText('admin');
    await expect(page.getByTestId('adminMenu')).toBeVisible();
    await logoutViaUI(page);
    await expect(page.getByTestId('adminMenu')).toHaveCount(0);
  });

  test('regular user can sign in without admin menu', async ({ page }) => {
    await loginViaUI(page, USERS.user.username, USERS.user.password);
    await page.goto('/');
    await expect(page.locator('#home-logged-message')).toContainText('user');
    await expect(page.getByTestId('adminMenu')).toHaveCount(0);
  });

  test('unauthenticated access to admin redirects to login', async ({ page }) => {
    await page.goto('/admin/user-management');
    await expect(page).toHaveURL(/\/login/);
    await expect(page.getByTestId('loginTitle')).toBeVisible();
  });
});
