import { ChangeDetectionStrategy, Component, HostListener, PLATFORM_ID, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink } from '@angular/router';

import { PRIMARY_CTA } from '../../content/company';

/**
 * A restrained, mobile only call to action.
 *
 * It appears once the visitor has scrolled past the hero, so it never covers
 * the first thing they came to read, and it hides again near the footer where
 * the full width version already sits.
 *
 * Two details keep it from getting in the way: it is only rendered on narrow
 * screens, and the page reserves space for it with a matching padding rule, so
 * it never covers a form control or the last line of content. The bar itself is
 * a landmark-free `aria-hidden` region with a real, focusable link inside, so
 * keyboard users reach the same destination from the normal page flow.
 */
@Component({
  selector: 'app-mobile-cta',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  template: `
    @if (visible()) {
      <div class="mcta" [class.mcta--out]="leavingFooter()">
        <a class="mcta__link" [routerLink]="link">
          <span class="mcta__label">{{ label }}</span>
          <span class="mcta__arrow" aria-hidden="true">&rarr;</span>
        </a>
      </div>
    }
  `,
  styles: `
    :host {
      display: contents;
    }

    /* Hidden entirely on wide screens, where the header CTA and the footer
       banner already do this job. */
    .mcta { display: none; }

    @media (max-width: 47.99rem) {
      .mcta {
        position: fixed;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 30;
        display: block;
        padding: 0.55rem 0.75rem calc(0.55rem + env(safe-area-inset-bottom, 0px));
        background: rgba(236, 233, 228, 0.94);
        backdrop-filter: blur(8px);
        border-top: 1px solid var(--line);
        transform: translateY(0);
        transition: transform var(--d-ui) var(--ease), opacity var(--d-ui) var(--ease);
      }

      /* Fades as the real footer CTA comes into view, so two CTAs never
         compete for the same corner. */
      .mcta--out { opacity: 0; transform: translateY(100%); }

      .mcta__link {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        width: 100%;
        padding: 0.85rem 1rem;
        border-radius: var(--radius-sm);
        background: var(--c-accent);
        color: var(--on-accent);
        font-family: var(--font-display);
        font-size: 0.78rem;
        font-weight: 600;
        letter-spacing: 0.1em;
        text-transform: uppercase;
        line-height: 1.25;
        text-align: center;
      }

      .mcta__link:hover { background: var(--c-accent-deep); }

      .mcta__label { min-width: 0; }
      .mcta__arrow { flex: none; }
    }

    @media (prefers-reduced-motion: reduce) {
      .mcta { transition: none; }
    }
  `,
})
export class MobileCta {
  protected readonly label = PRIMARY_CTA.label;
  protected readonly link = PRIMARY_CTA.link;

  private readonly platformId = inject(PLATFORM_ID);

  protected readonly visible = signal(false);
  protected readonly leavingFooter = signal(false);

  constructor() {
    this.evaluate();
  }

  /**
   * One scroll listener writing two booleans. The signal only notifies OnPush
   * when a value actually changes, so this does not re-render on every event.
   */
  @HostListener('window:scroll')
  protected onScroll(): void {
    this.evaluate();
  }

  private evaluate(): void {
    if (!isPlatformBrowser(this.platformId) || typeof window === 'undefined') {
      return;
    }

    const scrolled = window.scrollY;
    const viewport = window.innerHeight;
    const documentHeight = document.documentElement.scrollHeight;

    // Shown once the hero is behind us, hidden again at the very bottom where
    // the footer already carries a full width call to action.
    this.visible.set(scrolled > viewport * 0.75 && documentHeight > viewport * 1.5);
    this.leavingFooter.set(scrolled + viewport > documentHeight - 160);
  }
}