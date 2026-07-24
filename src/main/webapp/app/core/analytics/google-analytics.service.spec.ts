import { describe, expect, it, vitest, beforeEach } from 'vitest';
import { DOCUMENT } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { Title } from '@angular/platform-browser';

import { environment } from 'environments/environment';

import { GA_MEASUREMENT_ID, GoogleAnalyticsService } from './google-analytics.service';

vitest.mock('environments/environment', () => ({
  environment: {
    VERSION: 'TEST',
    DEBUG_INFO_ENABLED: false,
  },
}));

describe('GoogleAnalyticsService', () => {
  let service: GoogleAnalyticsService;
  let gtag: ReturnType<typeof vitest.fn>;
  let appendChild: ReturnType<typeof vitest.fn>;
  let createElement: ReturnType<typeof vitest.fn>;
  let analyticsWindow: { gtag?: typeof gtag; dataLayer?: unknown[]; location: { href: string } };
  let scriptEl: { async: boolean; src: string };

  beforeEach(() => {
    gtag = vitest.fn();
    appendChild = vitest.fn();
    scriptEl = { async: false, src: '' };
    createElement = vitest.fn().mockReturnValue(scriptEl);
    analyticsWindow = {
      gtag,
      location: { href: 'https://pass.argorand.io/pass-contract' },
    };
    (environment as { DEBUG_INFO_ENABLED: boolean }).DEBUG_INFO_ENABLED = false;

    TestBed.configureTestingModule({
      providers: [
        { provide: Title, useValue: { getTitle: () => 'Test Title' } },
        {
          provide: DOCUMENT,
          useValue: {
            defaultView: analyticsWindow,
            head: { appendChild },
            createElement,
          },
        },
      ],
    });
    service = TestBed.inject(GoogleAnalyticsService);
  });

  it('should send page_view with path and title', () => {
    service.trackPageView('/pass-contract?page=1');

    expect(gtag).toHaveBeenCalledWith('event', 'page_view', {
      page_title: 'Test Title',
      page_path: '/pass-contract',
      page_location: 'https://pass.argorand.io/pass-contract',
      send_to: GA_MEASUREMENT_ID,
    });
  });

  it('should no-op when gtag is missing', () => {
    delete analyticsWindow.gtag;
    expect(() => service.trackPageView('/')).not.toThrow();
    expect(gtag).not.toHaveBeenCalled();
  });

  it('should send search event with term and context', () => {
    service.trackSearch('  police  ', 'pass-contract');

    expect(gtag).toHaveBeenCalledWith('event', 'search', {
      search_term: 'police',
      search_context: 'pass-contract',
      send_to: GA_MEASUREMENT_ID,
    });
  });

  it('should ignore blank search terms', () => {
    service.trackSearch('   ', 'pass-contract');
    expect(gtag).not.toHaveBeenCalled();
  });

  it('should no-op tracking when DEBUG_INFO_ENABLED', () => {
    (environment as { DEBUG_INFO_ENABLED: boolean }).DEBUG_INFO_ENABLED = true;

    service.trackPageView('/pass-contract');
    service.trackSearch('police', 'pass-contract');

    expect(gtag).not.toHaveBeenCalled();
  });

  it('should inject gtag script on init when enabled', () => {
    delete analyticsWindow.gtag;

    service.init();

    expect(createElement).toHaveBeenCalledWith('script');
    expect(scriptEl.async).toBe(true);
    expect(scriptEl.src).toBe(`https://www.googletagmanager.com/gtag/js?id=${GA_MEASUREMENT_ID}`);
    expect(appendChild).toHaveBeenCalledWith(scriptEl);
    expect(typeof analyticsWindow.gtag).toBe('function');
  });

  it('should not inject gtag when DEBUG_INFO_ENABLED', () => {
    (environment as { DEBUG_INFO_ENABLED: boolean }).DEBUG_INFO_ENABLED = true;

    service.init();

    expect(createElement).not.toHaveBeenCalled();
    expect(appendChild).not.toHaveBeenCalled();
  });
});
