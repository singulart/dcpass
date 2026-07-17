import { expect, type APIRequestContext, type Page } from '@playwright/test';

export const USERS = {
  admin: { username: 'admin', password: 'admin' },
  user: { username: 'user', password: 'user' },
} as const;

const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';

export async function loginViaUI(page: Page, username: string, password: string): Promise<void> {
  await page.goto('/login');
  await page.getByTestId('username').fill(username);
  await page.getByTestId('password').fill(password);
  await page.getByTestId('submit').click();
  await expect(page).not.toHaveURL(/\/login$/);
  await page.getByTestId('accountMenu').click();
  await expect(page.getByTestId('logout')).toBeVisible();
  await page.keyboard.press('Escape');
}

export async function logoutViaUI(page: Page): Promise<void> {
  await page.getByTestId('accountMenu').click();
  await page.getByTestId('logout').click();
  await page.getByTestId('accountMenu').click();
  await expect(page.getByTestId('login')).toBeVisible();
  await page.keyboard.press('Escape');
}

export async function authenticateApi(
  request: APIRequestContext,
  username: string,
  password: string,
): Promise<string> {
  const response = await request.post(`${backendURL}/api/authenticate`, {
    data: { username, password, rememberMe: false },
  });
  expect(response.ok()).toBeTruthy();
  const body = (await response.json()) as { id_token: string };
  expect(body.id_token).toBeTruthy();
  return body.id_token;
}

export function authHeaders(token: string): Record<string, string> {
  return {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
  };
}
