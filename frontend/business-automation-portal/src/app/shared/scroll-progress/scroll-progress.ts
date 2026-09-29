import { ChangeDetectionStrategy, Component, HostListener, PLATFORM_ID, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

/**
 * A hairline scroll progress indicator.
 *
 * Deliberately minimal: a single 2px bar, transform only, and it never
 * intercepts a click. It is `aria-hidden` because it conveys nothing a screen
 * reader user needs, and it is hidden from print.
 *
 * The value is a percentage written to a CSS custom property rather than a
 * width, so the update never triggers layout.
 */
@Component({
  selector: 'app-scroll-progress',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="progress" aria-hidden="true">
      <span class="progress__bar" [style.--progress.%]="percent()"></span>
    </div>
  `,
  styles: `
    :host {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 40;
      display: block;
      height: 2px;
      pointer-events: none;
    }

    .progress { width: 100%; height: 100%; background: transparent; }

    .progress__bar {
      display: block;
      height: 100%;
      background: var(--c-accent);
      /* Scale rather than width: no layout, and it composites on the GPU. */
      transform: scaleX(var(--progress, 0));
      transform-origin: left;
      will-change: transform;
    }

    @media print {
      :host { display: none; }
    }
  `,
})
export class ScrollProgress {
  private readonly platformId = inject(PLATFORM_ID);

  /** 0 to 1. */
  protected readonly percent = signal(0);

  constructor() {
    this.evaluate();
  }

  @HostListener('window:scroll')
  protected onScroll(): void {
    this.evaluate();
  }

  private evaluate(): void {
    if (!isPlatformBrowser(this.platformId) || typeof window === 'undefined') {
      return;
    }

    const scrollable = document.documentElement.scrollHeight - window.innerHeight;
    if (scrollable <= 0) {
      this.percent.set(0);
      return;
    }

    const ratio = window.scrollY / scrollable;
    // Clamped so a rubber-band overscroll on iOS cannot exceed 1.
    this.percent.set(Math.min(1, Math.max(0, ratio)));
  }
}