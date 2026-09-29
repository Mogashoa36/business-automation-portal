import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/**
 * Page heading for the administration portal.
 *
 * One component so every admin page has the same hierarchy, and so the
 * descriptive text is always associated with the heading for screen readers.
 */
@Component({
  selector: 'app-admin-page-header',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header class="page-head">
      <div class="page-head__text">
        <h1 class="page-head__title">{{ title() }}</h1>
        @if (intro()) {
          <p class="page-head__intro">{{ intro() }}</p>
        }
      </div>
      <div class="page-head__actions">
        <ng-content />
      </div>
    </header>
  `,
  styles: `
    :host { display: block; }

    .page-head {
      display: flex;
      flex-wrap: wrap;
      align-items: flex-end;
      justify-content: space-between;
      gap: 1rem;
      margin-bottom: 1.5rem;
    }

    .page-head__text { min-width: 0; }

    .page-head__title {
      margin: 0 0 0.25rem;
      font-family: var(--font-display);
      font-size: clamp(1.3rem, 1.1rem + 1vw, 1.7rem);
      letter-spacing: -0.02em;
      color: var(--c-ink);
    }

    .page-head__intro {
      margin: 0;
      max-width: 62ch;
      color: var(--c-ink-soft);
      font-size: 0.92rem;
    }

    .page-head__actions {
      display: flex;
      flex-wrap: wrap;
      gap: 0.6rem;
      min-width: 0;
    }
  `,
})
export class AdminPageHeader {
  readonly title = input.required<string>();
  readonly intro = input<string>();
}