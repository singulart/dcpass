import { test as base, expect } from '@playwright/test';
import { addCoverageReport } from 'monocart-reporter';

const coverageEnabled = process.env.E2E_COVERAGE === '1' || process.env.E2E_COVERAGE === 'true';

/**
 * Extends Playwright's test with automatic V8 JS/CSS coverage collection when
 * E2E_COVERAGE=1 (see `npm run e2e:coverage`).
 */
export const test = base.extend({
  autoCoverage: [
    async ({ page }, use, testInfo) => {
      if (!coverageEnabled) {
        await use();
        return;
      }

      await Promise.all([
        page.coverage.startJSCoverage({
          resetOnNavigation: false,
          reportAnonymousScripts: false,
        }),
        page.coverage.startCSSCoverage({ resetOnNavigation: false }),
      ]);

      await use();

      const [jsCoverage, cssCoverage] = await Promise.all([page.coverage.stopJSCoverage(), page.coverage.stopCSSCoverage()]);
      const coverageList = [...jsCoverage, ...cssCoverage];
      if (coverageList.length > 0) {
        await addCoverageReport(coverageList, testInfo);
      }
    },
    { auto: true },
  ],
});

export { expect };
