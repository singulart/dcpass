import { defineConfig, devices } from '@playwright/test';

const frontendURL = process.env.E2E_BASE_URL ?? 'http://localhost:4200';
const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';
const coverageEnabled = process.env.E2E_COVERAGE === '1' || process.env.E2E_COVERAGE === 'true';

const coverageOptions = {
  name: 'DC PASS E2E Coverage',
  outputDir: './coverage-e2e',
  reports: ['v8', 'console-summary', 'lcov'] as const,
  entryFilter: (entry: { url?: string; source?: string }) => {
    const url = entry.url ?? '';
    if (!url || url.includes('node_modules') || url.includes('vite/client') || url.includes('@vite/') || url.includes('swagger-ui')) {
      return false;
    }
    // Prefer JS bundles (with source maps) over SPA document/HTML route URLs
    const isScript = /\.(m?js|ts)(\?|$)/.test(url) || Boolean(entry.source);
    return isScript && (url.includes('localhost:4200') || url.includes('localhost:8080'));
  },
  sourceFilter: (sourcePath: string) => {
    const normalized = sourcePath.replace(/\\/g, '/');
    if (normalized.includes('node_modules') || normalized.includes('swagger-ui')) {
      return false;
    }
    return (
      normalized.includes('src/main/webapp/app/') ||
      normalized.includes('src/main/webapp/content/') ||
      (normalized.startsWith('app/') && !normalized.includes('node_modules'))
    );
  },
  sourcePath: (filePath: string) => {
    const normalized = filePath.replace(/\\/g, '/');
    const marker = 'src/main/webapp/';
    const idx = normalized.indexOf(marker);
    if (idx >= 0) {
      return normalized.slice(idx);
    }
    if (normalized.startsWith('app/')) {
      return `src/main/webapp/${normalized}`;
    }
    return normalized;
  },
};

/**
 * Live-app E2E suite. Expects frontend (:4200) and backend (:8080) already running:
 *   ./npmw run backend:start
 *   ./npmw run start
 *
 * Coverage: ./npmw run e2e:coverage  →  ./npmw run e2e:coverage:open
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: 1,
  reporter: [
    ['list'],
    ['html', { open: 'never', outputFolder: 'playwright-report' }],
    ...(coverageEnabled
      ? [
          [
            'monocart-reporter',
            {
              name: 'DC PASS E2E',
              outputFile: './coverage-e2e/monocart-report.html',
              coverage: coverageOptions,
            },
          ] as const,
        ]
      : []),
  ],
  timeout: 60_000,
  expect: { timeout: 15_000 },
  use: {
    baseURL: frontendURL,
    testIdAttribute: 'data-cy',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    actionTimeout: 15_000,
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  globalSetup: './e2e/global-setup.ts',
  metadata: { frontendURL, backendURL, coverageEnabled },
});
