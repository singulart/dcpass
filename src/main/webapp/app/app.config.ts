import { provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { APP_INITIALIZER, ApplicationConfig, LOCALE_ID, importProvidersFrom, inject } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';
import {
  NavigationError,
  Router,
  RouterFeatures,
  TitleStrategy,
  provideRouter,
  withComponentInputBinding,
  withDebugTracing,
  withNavigationErrorHandler,
} from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';

import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'environments/environment';

import { authExpiredInterceptor } from 'app/core/interceptor/auth-expired.interceptor';
import { authInterceptor } from 'app/core/interceptor/auth.interceptor';
import { errorHandlerInterceptor } from 'app/core/interceptor/error-handler.interceptor';
import { notificationInterceptor } from 'app/core/interceptor/notification.interceptor';

import './config/dayjs';

import { AppPageTitleStrategy } from './app-page-title-strategy';
import routes from './app.routes';
import { NgbDateDayjsAdapter } from './config/datepicker-adapter';

const routerFeatures: RouterFeatures[] = [
  withComponentInputBinding(),
  withNavigationErrorHandler((e: NavigationError) => {
    const router = inject(Router);
    if (e.error.status === 403) {
      router.navigate(['/accessdenied']);
    } else if (e.error.status === 404) {
      router.navigate(['/404']);
    } else if (e.error.status === 401) {
      router.navigate(['/login']);
    } else {
      router.navigate(['/error']);
    }
  }),
];
if (environment.DEBUG_INFO_ENABLED) {
  routerFeatures.push(withDebugTracing());
}

/** ChatGPT MCP widget: SPA shell sets this flag so we open {@code /contracts-widget} inside the host sandbox. */
function contractsWidgetChatgptEmbedInitializer(router: Router): () => Promise<void> {
  return () => {
    const w = window as unknown as { __DCPASS_EMBED_CONTRACTS_WIDGET__?: boolean };
    if (w.__DCPASS_EMBED_CONTRACTS_WIDGET__) {
      return router.navigateByUrl('/contracts-widget', { replaceUrl: true }).then(() => undefined);
    }
    return Promise.resolve();
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, ...routerFeatures),
    { provide: APP_INITIALIZER, useFactory: contractsWidgetChatgptEmbedInitializer, deps: [Router], multi: true },
    importProvidersFrom(BrowserModule),
    // Set this to true to enable service worker (PWA)
    importProvidersFrom(ServiceWorkerModule.register('ngsw-worker.js', { enabled: false })),
    provideHttpClient(
      withInterceptors([authInterceptor, authExpiredInterceptor, errorHandlerInterceptor, notificationInterceptor]),
      withInterceptorsFromDi(),
    ),
    Title,
    { provide: LOCALE_ID, useValue: 'en' },
    { provide: NgbDateAdapter, useClass: NgbDateDayjsAdapter },
    { provide: TitleStrategy, useClass: AppPageTitleStrategy },
  ],
};
