import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Icon } from '../icon/icon';
import { Reveal } from '../reveal/reveal';

/**
 * Closing call to action, repeated at the end of the main pages. This is one
 * of the few deliberate dark moments on the site, so the accent appears only
 * on the button itself.
 */
@Component({
  selector: 'app-cta-banner',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, Reveal],
  template: `
    <section class="cta theme-dark" appReveal>
      <div class="container cta__inner">
        <span class="cta__line" aria-hidden="true"></span>
        <h2 class="cta__title">{{ title() }}</h2>
        <div class="cta__body">
          @if (text()) {
            <p class="lead">{{ text() }}</p>
          }
          <div class="btn-row">
            <a class="btn btn--primary" [routerLink]="ctaLink()">
              {{ ctaLabel() }}
              <app-icon name="arrow-right" [size]="18" />
            </a>
            @if (secondaryLabel()) {
              <a class="btn btn--outline" [routerLink]="secondaryLink()">{{ secondaryLabel() }}</a>
            }
          </div>
        </div>
      </div>
    </section>
  `,
  styles: `
    .cta {
      position: relative;
      padding-block: clamp(3.5rem, 8vw, 7rem);
      background-color: var(--c-dark);
      overflow: hidden;
    }

    /* A single restrained warm wash, plus one hairline rule for structure. */
    .cta::before {
      content: '';
      position: absolute;
      inset: 0;
      background: radial-gradient(70% 100% at 12% 0%, rgba(169, 71, 53, 0.22) 0%, rgba(169, 71, 53, 0) 60%);
      pointer-events: none;
    }

    .cta__inner {
      position: relative;
      display: grid;
      gap: clamp(1.75rem, 4vw, 4rem);
      min-width: 0;
    }

    /*
     * A single accent rule that draws in once the section scrolls into view.
     * ScaleX only, so it never affects layout, and it stops where it is.
     */
    .cta__line {
      position: absolute;
      top: 0;
      left: 0;
      width: 4.5rem;
      height: 2px;
      background: var(--c-accent-light);
      transform: scaleX(0);
      transform-origin: left;
      transition: transform 600ms var(--ease) 120ms;
    }

    .cta.is-revealed .cta__line { transform: scaleX(1); }

    .cta__title {
      margin: 0;
      max-width: 22ch;
      color: #f2efea;
      font-size: clamp(1.6rem, 1.1rem + 2.4vw, 3.2rem);
      line-height: 1.04;
      letter-spacing: -0.03em;
    }

    .lead {
      color: #bdb4ab;
      margin-bottom: 1.75rem;
      max-width: 46ch;
    }

    @media (min-width: 62rem) {
      .cta__inner {
        grid-template-columns: minmax(0, 1.1fr) minmax(0, 1fr);
        align-items: center;
      }
    }
  `,
})
export class CtaBanner {
  readonly title = input.required<string>();
  readonly text = input<string>();
  readonly ctaLabel = input.required<string>();
  readonly ctaLink = input.required<string>();
  readonly secondaryLabel = input<string>();
  readonly secondaryLink = input<string>('/services');
}
