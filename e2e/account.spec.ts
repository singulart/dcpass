import { expect, test } from './fixtures';
import { loginViaUI, logoutViaUI, USERS } from './helpers/auth';

test.describe('Account', () => {
  test('settings page loads and saves profile for admin', async ({ page }) => {
    await loginViaUI(page, USERS.admin.username, USERS.admin.password);
    await page.getByTestId('accountMenu').click();
    await page.getByTestId('settings').click();

    await expect(page.getByTestId('firstname')).toBeVisible();
    const firstName = (await page.getByTestId('firstname').inputValue()) || 'Administrator';
    await page.getByTestId('firstname').fill(firstName);
    await page.getByTestId('submit').click();
    await expect(page.getByText('Settings saved!')).toBeVisible();
  });

  test('password page is reachable', async ({ page }) => {
    await loginViaUI(page, USERS.admin.username, USERS.admin.password);
    await page.getByTestId('accountMenu').click();
    await page.getByTestId('passwordItem').click();
    await expect(page.getByTestId('currentPassword')).toBeVisible();
    await expect(page.getByTestId('newPassword')).toBeVisible();
    await expect(page.getByTestId('confirmPassword')).toBeVisible();
  });

  test('user can change password and sign in with the new one', async ({ page }) => {
    const newPassword = 'Password2!';

    await loginViaUI(page, USERS.user.username, USERS.user.password);
    await page.goto('/account/password');
    await page.getByTestId('currentPassword').fill(USERS.user.password);
    await page.getByTestId('newPassword').fill(newPassword);
    await page.getByTestId('confirmPassword').fill(newPassword);
    await page.getByTestId('submit').click();
    await expect(page.locator('.alert-success')).toBeVisible();
    await logoutViaUI(page);

    await loginViaUI(page, USERS.user.username, newPassword);
    await page.goto('/account/password');
    await page.getByTestId('currentPassword').fill(newPassword);
    await page.getByTestId('newPassword').fill(USERS.user.password);
    await page.getByTestId('confirmPassword').fill(USERS.user.password);
    await page.getByTestId('submit').click();
    await expect(page.locator('.alert-success')).toBeVisible();
    await logoutViaUI(page);

    await loginViaUI(page, USERS.user.username, USERS.user.password);
  });
});
