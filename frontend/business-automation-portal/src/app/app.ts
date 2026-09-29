import { Component, computed, inject, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

import { Footer } from './layout/footer/footer';
import { Header } from './layout/header/header';
import { MobileCta } from './shared/mobile-cta/mobile-cta';
import { ScrollProgress } from './shared/scroll-progress/scroll-progress';

/**
 * Application shell: skip link, sticky header, routed content region and footer.
 *
 * The marketing chrome is deliberately hidden on `/admin`. The administration
 * portal has its own shell, and showing a public navigation bar inside an
 * authenticated tool would be both confusing and a needless leak of the
 * marketing structure. This is presentation only; the admin routes are
 * protected by the guard and the API regardless.
 */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, MobileCta, ScrollProgress],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  /** True while an administration route is active. */
  protected readonly isAdminRoute = computed(() => this.currentUrl().startsWith('/admin'));

  private readonly currentUrl = signal('/');

  constructor() {
    const router = inject(Router);

    this.trackUrl(router);

    // Start every new page at the top. Guarded so it only runs in a browser -
    // server side rendering has no window and must not touch the DOM.
    if (isPlatformBrowser(inject(PLATFORM_ID))) {
      router.events
        .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
        .subscribe(() => window.scrollTo({ top: 0, left: 0, behavior: 'auto' }));
    }
  }

  private trackUrl(router: Router): void {
    this.currentUrl.set(router.url);
    router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => this.currentUrl.set(event.urlAfterRedirects));
  }
}

