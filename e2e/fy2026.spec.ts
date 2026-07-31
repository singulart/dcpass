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
  test('clicks through agency, contract, and purchase order charts', async ({ page }) => {
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

    await page.route('**/api/fy2026/it-awarded-by-agency', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([{ agency: 'ABC — Alpha Agency', agencyAcronym: 'ABC', spend: 500_000 }]),
      });
    });

    await page.goto('/fy2026');
    await expect(page.getByRole('heading', { name: 'FY2026 IT Spend', exact: true })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Actual IT payments', exact: true })).toBeVisible();
    await expect(page.getByTestId('fy2026Heading')).toHaveText('FY2026 IT spend by agency');
    await expect(page.getByTestId('fy2026TopAgencyChart')).toBeVisible();
    await expect(page.getByTestId('fy2026TopAgencyLegend')).toContainText('ABC — Alpha Agency');

    await clickChartElement(page, 'fy2026TopAgencyChart', 0);
    await expect(page.getByTestId('fy2026Heading')).toContainText('IT spend by contract');
    await expect(page.getByTestId('fy2026DrillChart')).toBeVisible();
    await expect(page.getByTestId('fy2026DrillChart')).toContainText('Prime IT Support');

    await page.getByTestId('fy2026DrillChart').getByText('Prime IT Support').click();
    await expect(page.getByTestId('fy2026Heading')).toContainText('IT spend by purchase order');
    await expect(page.getByTestId('fy2026DrillChart')).toBeVisible();
    await expect(page.getByTestId('fy2026DrillChart')).toContainText('PO-101 — Network Gear');

    const popupPromise = page.waitForEvent('popup');
    await page.getByTestId('fy2026DrillChart').getByText('PO-101 — Network Gear').click();
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

  test('clicks through awarded agency to contract chart', async ({ page }) => {
    await page.route('**/api/fy2026/it-spend-by-agency', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([{ agency: 'ABC — Alpha Agency', agencyAcronym: 'ABC', spend: 100_000 }]),
      });
    });

    await page.route('**/api/fy2026/it-awarded-by-agency', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          { agency: 'ABC — Alpha Agency', agencyAcronym: 'ABC', spend: 3_100_000 },
          { agency: 'XYZ — Zeta Agency', agencyAcronym: 'XYZ', spend: 1_800_000 },
        ]),
      });
    });

    await page.route('**/api/fy2026/it-awarded-by-contract**', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          { contractTitle: 'Enterprise Licensing', contractNumber: 'CW99999', spend: 2_000_000 },
          { contractTitle: 'Helpdesk Staffing', contractNumber: 'CW88888', spend: 750_000 },
          { contractTitle: 'NO CONTRACT', contractNumber: 'CW77777', spend: 400_000 },
          { contractTitle: 'Small Addon', contractNumber: 'CW00002', spend: 9_000 },
        ]),
      });
    });

    await page.goto('/fy2026');
    await expect(page.getByRole('heading', { name: 'Awarded IT task orders', exact: true })).toBeVisible();
    await expect(page.getByTestId('fy2026AwardedHeading')).toHaveText('FY2026 awarded IT task orders by agency');
    await expect(page.getByTestId('fy2026AwardedTopAgencyChart')).toBeVisible();
    await expect(page.getByTestId('fy2026AwardedTopAgencyLegend')).toContainText('ABC — Alpha Agency');

    await page.route('**/api/pass-contracts**', async route => {
      const url = new URL(route.request().url());
      if (url.searchParams.get('contractNumber.equals') === 'CW99999') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify([{ id: 55, contractNumber: 'CW99999', title: 'Enterprise Licensing' }]),
        });
        return;
      }
      await route.fulfill({ status: 200, contentType: 'application/json', body: '[]' });
    });

    await page.getByTestId('fy2026AwardedTopAgencyLegend').getByText('ABC — Alpha Agency').click();
    await expect(page.getByTestId('fy2026AwardedDrillChart')).toBeVisible();
    await expect(page.getByTestId('fy2026AwardedDrillChart')).toContainText('Enterprise Licensing');
    await expect(page.getByText(/ranked by awarded purchase order dollars/i)).toBeVisible();
    await expect(page.getByTestId('fy2026AwardedUnmatchedLegend')).toContainText('do not match a contract');
    await expect(page.getByTestId('fy2026AwardedUnmatchedLegend').getByRole('link', { name: 'Open Data DC' })).toHaveAttribute(
      'href',
      'https://opendata.dc.gov/',
    );

    const popupPromise = page.waitForEvent('popup');
    await page.getByTestId('fy2026AwardedDrillChart').getByText('Enterprise Licensing').click();
    const popup = await popupPromise;
    await popup.waitForLoadState();
    await expect(popup).toHaveURL(/\/pass-contract\/55\/view/);
    await popup.close();

    await page.getByRole('button', { name: 'Back' }).last().click();
    await expect(page.getByTestId('fy2026AwardedHeading')).toHaveText('FY2026 awarded IT task orders by agency');
    await expect(page.getByTestId('fy2026AwardedTopAgencyChart')).toBeVisible();
  });
});
