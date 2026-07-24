import { DOCUMENT } from '@angular/common';
import { Injectable, inject } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { environment } from 'environments/environment';

/** GA4 measurement ID. */
export const GA_MEASUREMENT_ID = 'G-4JN7D8HE1X';

type GtagFn = (...args: unknown[]) => void;

interface AnalyticsWindow extends Window {
  dataLayer?: unknown[];
  gtag?: GtagFn;
}

@Injectable({ providedIn: 'root' })
export class GoogleAnalyticsService {
  private readonly title = inject(Title);
  private readonly document = inject(DOCUMENT);
  private initialized = false;

  /** GA is off for local/dev Angular builds (`DEBUG_INFO_ENABLED`). */
  get enabled(): boolean {
    return !environment.DEBUG_INFO_ENABLED;
  }

  /**
   * Load gtag.js and configure GA4. No-ops when {@link enabled} is false.
   * Pageviews are owned by Angular ({@code send_page_view: false}).
   */
  init(): void {
    if (!this.enabled || this.initialized) {
      return;
    }

    const win = this.document.defaultView as AnalyticsWindow | null;
    if (!win) {
      return;
    }

    win.dataLayer = win.dataLayer ?? [];
    // Official gtag snippet shape so the async loader can replay queued calls.
    win.gtag = function gtag(...args: unknown[]) {
      win.dataLayer!.push(args);
    };

    win.gtag('js', new Date());
    win.gtag('config', GA_MEASUREMENT_ID, { send_page_view: false });

    const script = this.document.createElement('script');
    script.async = true;
    script.src = `https://www.googletagmanager.com/gtag/js?id=${GA_MEASUREMENT_ID}`;
    this.document.head.appendChild(script);
    this.initialized = true;
  }

  /**
   * Send a GA4 page_view for SPA navigations.
   */
  trackPageView(url: string): void {
    const gtag = this.gtagFn();
    if (!gtag) {
      return;
    }

    const path = url.split(/[?#]/)[0] || '/';
    const win = this.document.defaultView;
    gtag('event', 'page_view', {
      page_title: this.title.getTitle(),
      page_path: path,
      page_location: win?.location.href ?? path,
      send_to: GA_MEASUREMENT_ID,
    });
  }

  /**
   * Send a GA4 {@code search} event when a user submits a list search.
   * @param searchTerm raw user input (trimmed; empty terms are ignored)
   * @param searchContext which entity list was searched (e.g. pass-contract)
   */
  trackSearch(searchTerm: string, searchContext: string): void {
    const term = searchTerm.trim();
    if (!term) {
      return;
    }

    const gtag = this.gtagFn();
    if (!gtag) {
      return;
    }

    gtag('event', 'search', {
      search_term: term,
      search_context: searchContext,
      send_to: GA_MEASUREMENT_ID,
    });
  }

  private gtagFn(): GtagFn | undefined {
    if (!this.enabled) {
      return undefined;
    }
    const win = this.document.defaultView as AnalyticsWindow | null;
    return typeof win?.gtag === 'function' ? win.gtag : undefined;
  }
}
