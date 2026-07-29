import type { Page } from '@playwright/test';
import { expect, test } from './fixtures';

type ChartCanvasElement = HTMLCanvasElement & {
  __fy2026Chart?: {
    getDatasetMeta: (datasetIndex: number) => {
      data: Array<{ getCenterPoint: () => { x: number; y: number } }>;
    };
  };
};

async function clickChartElement(page: Page, testId: string, elementIndex: number): Promise<void> {
  const canvas = page.getByTestId(testId);
  await expect(canvas).toBeVisible();
  await expect
    .poll(async () =>
      canvas.evaluate((el, index) => {
        const chart = (el as ChartCanvasElement).__fy2026Chart;
        return chart?.getDatasetMeta(0)?.data?.[index] != null;
      }, elementIndex),
    )
    .toBe(true);

  const point = await canvas.evaluate((el, index) => {
    const chart = (el as ChartCanvasElement).__fy2026Chart!;
    return chart.getDatasetMeta(0).data[index].getCenterPoint();
  }, elementIndex);

  await canvas.click({ position: point });
}

test.describe('FY2026 IT spend charts', () => {
  test('clicks through agency, contract, and purchase-order charts', async ({ page }) => {
    await page.route('**/api/fy2026/it-spend-by-agency', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          { agency: 'ABC — Alpha Agency', agencyAcronym: 'ABC', spend: 2_500_000 },
          { agency: 'XYZ — Zeta Agency', agencyAcronym: 'XYZ', spend: 1_200_000 },
        ]),
      });
    });

    await page.route('**/api/fy2026/it-spend-by-contract**', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          { contractTitle: 'Prime IT Support', contractNumber: 'CW12345', spend: 900_000 },
          { contractTitle: 'Cloud Hosting', contractNumber: 'CW67890', spend: 350_000 },
          { contractTitle: 'Small Widget', contractNumber: 'CW00001', spend: 12_000 },
        ]),
      });
    });

    await page.route('**/api/fy2026/it-spend-by-po**', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          { purchaseOrderId: 101, poNumber: 'PO-101', poTitle: 'Network Gear', spend: 400_000 },
          { purchaseOrderId: 102, poNumber: 'PO-102', poTitle: 'Laptops', spend: 275_000 },
          { purchaseOrderId: 103, poNumber: 'PO-103', poTitle: 'Cables', spend: 8_000 },
        ]),
      });
    });

    await page.goto('/fy2026');
    await expect(page.getByRole('heading', { name: 'FY2026 IT Spend', exact: true })).toBeVisible();
    await expect(page.getByTestId('fy2026Heading')).toHaveText('FY2026 IT spend by agency');
    await expect(page.getByTestId('fy2026TopAgencyChart')).toBeVisible();
    await expect(page.getByText('Click an agency slice to drill down')).toBeVisible();

    await clickChartElement(page, 'fy2026TopAgencyChart', 0);
    await expect(page.getByTestId('fy2026Heading')).toContainText('IT spend by contract');
    await expect(page.getByTestId('fy2026DrillChart')).toBeVisible();
    await expect(page.getByText(/Contracts under \$50k/i)).toBeVisible();

    await clickChartElement(page, 'fy2026DrillChart', 0);
    await expect(page.getByTestId('fy2026Heading')).toContainText('IT spend by purchase order');
    await expect(page.getByTestId('fy2026DrillChart')).toBeVisible();
    await expect(page.getByText(/POs under \$50k/i)).toBeVisible();

    const popupPromise = page.waitForEvent('popup');
    await clickChartElement(page, 'fy2026DrillChart', 0);
    const popup = await popupPromise;
    await popup.waitForLoadState();
    await expect(popup).toHaveURL(/\/purchase-order\/101\/view/);
    await popup.close();

    await page.getByRole('button', { name: 'Back' }).click();
    await expect(page.getByTestId('fy2026Heading')).toContainText('IT spend by contract');

    await page.getByRole('button', { name: 'Agencies', exact: true }).click();
    await expect(page.getByTestId('fy2026Heading')).toHaveText('FY2026 IT spend by agency');
    await expect(page.getByTestId('fy2026TopAgencyChart')).toBeVisible();
  });
});
