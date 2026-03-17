import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';

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
  private readonly router = inject(Router);
  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);

  /** Minimal layout (no navbar/footer) for embedded widget route. */
  isWidgetRoute = signal(false);

  ngOnInit(): void {
    this.updateWidgetRoute();
    this.router.events.subscribe(() => this.updateWidgetRoute());
    this.accountService.identity().subscribe();
  }

  private updateWidgetRoute(): void {
    this.isWidgetRoute.set(this.router.url.includes('/contracts-widget'));
  }
}
