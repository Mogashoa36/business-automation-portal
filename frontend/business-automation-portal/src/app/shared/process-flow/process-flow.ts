import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/**
 * Renders an ordered process as a numbered pipeline. Steps sit in a wrapping
 * row on wide screens and a vertical rail on narrow screens, so the same
 * component serves a six step approval flow and a three step build process.
 */
@Component({
  selector: 'app-process-flow',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (title()) {
      <p class="flow__title">{{ title() }}</p>
    }
    <ol class="flow">
      @for (step of steps(); track step; let last = $last) {
        <li class="flow__step" [class.flow__step--last]="last">
          <span class="flow__num" aria-hidden="true">{{ pad($index + 1) }}</span>
          <span class="flow__label">{{ step }}</span>
        </li>
      }
    </ol>
  `,
  styles: `
    :host { display: block; }

    .flow__title {
      margin: 0 0 1.75rem;
      font-family: var(--font-display);
      font-size: var(--fs-xs);
      font-weight: 600;
      letter-spacing: 0.2em;
      text-transform: uppercase;
      color: var(--accent-text);
    }

    .flow {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(min(100%, 11rem), 1fr));
      gap: 1px;
      margin: 0;
      padding: 0;
      list-style: none;
      background: var(--line);
      border: 1px solid var(--line);
      border-radius: var(--radius);
      overflow: hidden;
    }

    .flow__step {
      position: relative;
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      min-width: 0;
      padding: clamp(1.15rem, 2vw, 1.6rem);
      background: var(--surface);
      transition: box-shadow var(--d-ui) var(--ease);
    }

    .flow__step:hover { box-shadow: var(--shadow); }

    /* Connector rule running from this step to the next. */
    .flow__step:not(.flow__step--last)::after {
      content: '';
      position: absolute;
      top: 50%;
      right: -1px;
      width: 1px;
      height: 2px;
      transform: translateY(-50%);
      background: var(--accent);
      opacity: 0.55;
      transition: opacity var(--d-ui) var(--ease), height var(--d-ui) var(--ease);
    }

    /* The connector brightens and grows a little under the pointer. */
    @media (hover: hover) {
      .flow__step:hover:not(.flow__step--last)::after { opacity: 1; height: 14px; }
    }

    .flow__num {
      font-family: var(--font-display);
      font-size: 0.7rem;
      font-weight: 600;
      letter-spacing: 0.16em;
      color: var(--accent);
    }

    .flow__label {
      font-family: var(--font-display);
      font-size: clamp(0.95rem, 0.9rem + 0.25vw, 1.12rem);
      font-weight: 500;
      letter-spacing: -0.015em;
      line-height: 1.25;
      color: var(--text);
    }

    /* Vertical rail on narrow screens. */
    @media (max-width: 44rem) {
      .flow { grid-template-columns: minmax(0, 1fr); }

      .flow__step { flex-direction: row; align-items: center; gap: 1.1rem; }

      .flow__step::before {
        content: '';
        position: absolute;
        top: -1px;
        left: 1.6rem;
        right: 1.6rem;
        height: 1px;
        background: var(--line);
      }

      .flow__step:not(.flow__step--last)::after {
        top: auto;
        right: auto;
        bottom: -1px;
        left: 1.9rem;
        width: 1.5rem;
        height: 1px;
        transform: none;
      }
    }

    /* Dark surfaces invert the step cards. */
    :host-context(.theme-dark) .flow__step { background: var(--c-dark-2); }
    :host-context(.theme-dark) .flow__step:hover { background: var(--c-dark-3); }
  `,
})
export class ProcessFlow {
  readonly steps = input.required<readonly string[]>();
  readonly title = input<string>();

  /** Zero padded step number, e.g. 01, 02. */
  protected pad(value: number): string {
    return value.toString().padStart(2, '0');
  }
}
