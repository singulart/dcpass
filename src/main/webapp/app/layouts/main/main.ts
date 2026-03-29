import { Location } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';

import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { AccountService } from 'app/core/auth/account.service';
import Footer from '../footer/footer';
import PageRibbon from '../profiles/page-ribbon';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.html',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet, Footer, PageRibbon],
})
export default class Main implements OnInit {
  /** Minimal layout (no navbar/footer) for embedded widget route. */
  isWidgetRoute = signal(false);

  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);

  /** Avoid GET /api/account in the embedded MCP widget; load once if the user leaves the widget shell. */
  private accountIdentityRequested = false;

  ngOnInit(): void {
    this.updateWidgetRoute();
    this.ensureAccountLoadedForShell();
    this.router.events.pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd)).subscribe(() => {
      this.updateWidgetRoute();
      this.ensureAccountLoadedForShell();
    });
  }

  private updateWidgetRoute(): void {
    this.isWidgetRoute.set(this.isContractsWidgetShell());
  }

  /**
   * Router URL can still be empty on first tick (non-blocking initial navigation); the browser path is correct
   * immediately for direct loads like {@code /contracts-widget}.
   */
  private isContractsWidgetShell(): boolean {
    const stripQueryAndHash = (u: string): string => u.split(/[?#]/)[0];
    const hasWidgetSegment = (path: string): boolean =>
      stripQueryAndHash(path)
        .split('/')
        .some(seg => seg === 'contracts-widget');
    return hasWidgetSegment(this.router.url) || hasWidgetSegment(this.location.path(false));
  }

  private ensureAccountLoadedForShell(): void {
    if (this.isContractsWidgetShell() || this.accountIdentityRequested) {
      return;
    }
    this.accountIdentityRequested = true;
    this.accountService.identity().subscribe();
  }
}
