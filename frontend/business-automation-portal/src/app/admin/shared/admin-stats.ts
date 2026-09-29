import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** One figure on the dashboard. The label states exactly what is counted. */
export interface AdminStat {
  readonly label: string;
  readonly value: number;
  readonly hint?: string;
  readonly tone?: 'default' | 'accent' | 'muted';
}

/**
 * The dashboard statistic tiles.
 *
 * Every value comes from a real database count. The component refuses to
 * invent or embellish a number, and the label makes clear what is being
 * counted so a figure is never ambiguous.
 */
@Component({
  selector: 'app-admin-stats',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <ul class="stats">
      @for (stat of stats(); track stat.label) {
        <li class="stat" [class]="'stat stat--' + (stat.tone ?? 'default')">
          <span class="stat__value">{{ stat.value }}</span>
          <span class="stat__label">{{ stat.label }}</span>
          @if (stat.hint) {
            <span class="stat__hint">{{ stat.hint }}</span>
          }
        </li>
      }
    </ul>
  `,
  styles: `
    :host { display: block; }

    .stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(min(100%, 9.5rem), 1fr));
      gap: 1px;
      margin: 0;
      padding: 0;
      list-style: none;
      background: var(--line);
      border: 1px solid var(--line);
      border-radius: var(--radius);
      overflow: hidden;
    }

    .stat {
      display: flex;
      flex-direction: column;
      gap: 0.2rem;
      min-width: 0;
      padding: 1rem 1.1rem;
      background: var(--c-white);
    }

    .stat__value {
      font-family: var(--font-display);
      font-size: 1.75rem;
      font-weight: 600;
      line-height: 1.1;
      letter-spacing: -0.02em;
      color: var(--c-ink);
      font-variant-numeric: tabular-nums;
    }

    .stat--accent .stat__value { color: var(--c-accent); }
    .stat--muted .stat__value { color: var(--c-ink-muted); }

    .stat__label {
      font-size: 0.78rem;
      font-weight: 600;
      color: var(--c-ink-soft);
    }

    .stat__hint {
      font-size: 0.72rem;
      color: var(--c-ink-muted);
    }
  `,
})
export class AdminStats {
  readonly stats = input.required<readonly AdminStat[]>();
}