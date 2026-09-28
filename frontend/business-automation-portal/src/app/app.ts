import { Component, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

import { Footer } from './layout/footer/footer';
import { Header } from './layout/header/header';

/**
 * Application shell: skip link, sticky header, routed content region and footer.
 */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  constructor() {
    const router = inject(Router);

    // Start every new page at the top. Guarded so it only runs in a browser -
    // server side rendering has no window and must not touch the DOM.
    if (isPlatformBrowser(inject(PLATFORM_ID))) {
      router.events
        .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
        .subscribe(() => window.scrollTo({ top: 0, left: 0, behavior: 'auto' }));
    }
  }
}

