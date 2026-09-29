import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * Hero used at the top of every inner page so the hierarchy and tone stay
 * consistent. The page title is always rendered as the page `h1`.
 */
@Component({
  selector: 'app-page-hero',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  template: `
    <section class="hero">
      <span class="hero__grid" aria-hidden="true"></span>
      <div class="container hero__inner">
        @if (crumbs().length) {
          <nav class="crumbs" aria-label="Breadcrumb">
            <ol>
              <li><a routerLink="/">Home</a></li>
              @for (crumb of crumbs(); track crumb.label) {
                <li>
                  <span aria-hidden="true">/</span>
                  @if (crumb.link) {
                    <a [routerLink]="crumb.link">{{ crumb.label }}</a>
                  } @else {
                    <span aria-current="page">{{ crumb.label }}</span>
                  }
                </li>
              }
            </ol>
          </nav>
        }

        @if (eyebrow()) {
          <p class="eyebrow" data-anim="fade-up">{{ eyebrow() }}</p>
        }
        <h1 data-anim="fade-up" class="d1">{{ title() }}</h1>
        <span class="hero__rule" aria-hidden="true"></span>
        @if (intro()) {
          <p class="lead d2" data-anim="fade-up">{{ intro() }}</p>
        }
      </div>
    </section>
  `,
  styles: `
    .hero {
      position: relative;
      padding-top: calc(var(--header-h) + clamp(2.75rem, 6vw, 5.5rem));
      padding-bottom: clamp(2.25rem, 4.5vw, 4rem);
      background-color: var(--c-cream);
      border-bottom: 1px solid var(--line);
      overflow: hidden;
    }

    /* A fading grid rather than a glow: quiet, technical, and legible. */
    .hero__grid {
      position: absolute;
      inset: 0;
      background-image: linear-gradient(rgba(33, 30, 27, 0.04) 1px, transparent 1px),
        linear-gradient(90deg, rgba(33, 30, 27, 0.04) 1px, transparent 1px);
      background-size: 5rem 5rem;
      mask-image: linear-gradient(160deg, #000 0%, transparent 68%);
      -webkit-mask-image: linear-gradient(160deg, #000 0%, transparent 68%);
      pointer-events: none;
    }

    .hero__inner {
      position: relative;
      z-index: 1;
      max-width: 60rem;
      min-width: 0;
    }

    /*
     * A short accent rule under the title, drawn in once as the page settles.
     * transform-only, so it never shifts the heading or affects layout.
     */
    .hero__rule {
      display: block;
      width: 3.5rem;
      height: 2px;
      margin-top: 1.75rem;
      background: var(--c-accent);
      animation: draw-line var(--d-emphasis) var(--ease) 320ms both;
    }

    h1 {
      margin-bottom: 1.25rem;
      font-size: var(--fs-h1);
      color: var(--c-ink);
    }

    .crumbs {
      margin-bottom: 1.75rem;
      font-size: 0.76rem;
      letter-spacing: 0.1em;
      text-transform: uppercase;
    }

    .crumbs ol {
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;
      list-style: none;
    }

    .crumbs li {
      display: flex;
      gap: 0.5rem;
      min-width: 0;
      color: var(--c-ink-soft);
    }

    .crumbs a { color: var(--c-ink-soft); }
    .crumbs a:hover { color: var(--c-accent); }
    .crumbs [aria-current] { color: var(--c-ink); font-weight: 600; }
  `,
})
export class PageHero {
  readonly eyebrow = input<string>();
  readonly title = input.required<string>();
  readonly intro = input<string>();
  readonly crumbs = input<readonly { label: string; link?: string }[]>([]);
}
