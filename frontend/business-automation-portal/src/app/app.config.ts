import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideRouter, withInMemoryScrolling } from '@angular/router';

import { routes } from './app.routes';
import { adminAuthInterceptor } from './admin/interceptors/admin-auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withInMemoryScrolling({ scrollPositionRestoration: 'enabled' })),
    provideClientHydration(withEventReplay()),
    // withFetch keeps requests SSR friendly: the server renders with the same
    // fetch based HttpClient the browser uses.
    // withInterceptors (rather than a class interceptor) is the Angular 21
    // idiom, and the admin interceptor is registered globally so an expired
    // session is handled the same way from every admin screen.
    provideHttpClient(withFetch(), withInterceptors([adminAuthInterceptor])),
  ],
};
