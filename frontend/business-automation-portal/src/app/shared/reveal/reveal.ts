import { isPlatformBrowser } from '@angular/common';
import { Directive, ElementRef, PLATFORM_ID, inject } from '@angular/core';

/**
 * Reveals an element the first time it scrolls into view.
 *
 * Three variants share one observer implementation:
 *   [appReveal]        the element itself fades up
 *   [appRevealGroup]   its direct children fade up in sequence (70ms apart)
 *   [appMockup]        its direct children assemble in sequence, so a
 *                      dashboard arrives in the order an app would draw it
 *
 * The animation itself lives in the global stylesheet and is scoped to `.js`,
 * so server rendered output and visitors without JavaScript see the content
 * immediately rather than waiting on an observer that never runs.
 *
 * One observer per element, disconnected on destroy. There is no scroll
 * listener, no timer and no polling anywhere in this file.
 */
@Directive({
  selector: '[appReveal], [appRevealGroup], [appMockup]',
})
export class Reveal {
  private readonly element = inject<ElementRef<HTMLElement>>(ElementRef);
  private readonly platformId = inject(PLATFORM_ID);
  private observer?: IntersectionObserver;

  ngOnInit(): void {
    const host = this.element.nativeElement;

    if (host.hasAttribute('appRevealGroup')) {
      host.setAttribute('data-reveal-group', '');
    } else if (host.hasAttribute('appMockup')) {
      host.setAttribute('data-mockup', '');
    } else {
      host.setAttribute('data-reveal', '');
    }

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    // Someone who asked for less motion gets the finished state immediately,
    // with no transform and no delay to wait out.
    const reduceMotion = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
    if (reduceMotion || typeof IntersectionObserver === 'undefined') {
      host.classList.add('is-revealed');
      return;
    }

    this.observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-revealed');
            this.observer?.unobserve(entry.target);
          }
        }
      },
      /*
       * `threshold: 0` fires as soon as any part of the target crosses into
       * view. A ratio would be wrong here: a section head or a card grid can
       * be taller than the viewport, and a ratio large enough to count as
       * "visible" may never be reached as the page scrolls past it. The
       * negative bottom margin supplies the small delay instead, so content
       * has settled before it starts to appear.
       */
      { threshold: 0, rootMargin: '0px 0px -6% 0px' },
    );

    this.observer.observe(host);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }
}
