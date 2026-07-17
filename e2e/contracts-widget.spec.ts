import { expect, test } from './fixtures';

test.describe('Contracts widget', () => {
  test('shows waiting state until tool result arrives', async ({ page }) => {
    await page.goto('/contracts-widget');
    await expect(page.getByText(/Waiting for contract results/i)).toBeVisible();
  });

  test('renders contracts from host toolOutput (openai:set_globals)', async ({ page }) => {
    await page.goto('/contracts-widget');
    await expect(page.getByText(/Waiting for contract results/i)).toBeVisible();

    // Standalone widget ignores same-window postMessage (requires event.source === parent).
    // ChatGPT delivers results via window.openai.toolOutput / openai:set_globals.
    await page.evaluate(() => {
      window.dispatchEvent(
        new CustomEvent('openai:set_globals', {
          detail: {
            globals: {
              toolOutput: {
                structuredContent: {
                  contracts: [
                    {
                      id: 9001,
                      title: 'Widget E2E Contract',
                      description: 'Rendered via host toolOutput',
                      contractNumber: 'WIDGET-E2E-1',
                      contractAmount: 1500000,
                      awardDate: '2025-06-01',
                      startDate: '2025-07-01',
                      endDate: '2026-06-30',
                      supplier: 'Widget Vendor',
                      contractStatus: 'Active',
                      agencyAcronym: 'E2E',
                      agencyName: 'Widget Agency',
                    },
                  ],
                  page: 0,
                  totalItems: 1,
                  size: 20,
                },
              },
            },
          },
        }),
      );
    });

    await expect(page.getByText('Widget E2E Contract')).toBeVisible();
    await page.getByText('Widget E2E Contract').click();
    await expect(page.getByText('Rendered via host toolOutput')).toBeVisible();
    await expect(page.getByText('WIDGET-E2E-1')).toBeVisible();
  });
});
