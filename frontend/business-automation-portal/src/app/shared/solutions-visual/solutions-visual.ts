import { ChangeDetectionStrategy, Component } from '@angular/core';

import { Reveal } from '../reveal/reveal';

/**
 * One composition showing how the five solutions feed each other.
 *
 * The brief asked for a single supporting visual for the solutions section
 * rather than a photograph per solution, and this is the honest way to do that:
 * an operations console where a request moves left to right through the stages,
 * with the reporting output at the end.
 *
 * Decorative, so it is `aria-hidden`. The chain it depicts is also written out
 * as text immediately below, so nothing is conveyed only by the image.
 */
@Component({
  selector: 'app-solutions-visual',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Reveal],
  template: `
    <div class="ops" appMockup aria-hidden="true">
      <div class="ops__bar">
        <span class="ops__dot"></span>
        <span class="ops__title">Operations — Live request overview</span>
      </div>

      <div class="ops__body">
        <div class="ops__lane">
          @for (step of stages; track step.label; let index = $index) {
            <div class="ops__step" [class.ops__step--done]="index < 2" [class.ops__step--now]="index === 2">
              <span class="ops__step-label">{{ step.label }}</span>
              <span class="ops__step-meta">{{ step.meta }}</span>
            </div>
            @if (!$last) {
              <span class="ops__arrow"></span>
            }
          }
        </div>

        <div class="ops__panel">
          <div class="ops__kpis">
            <div class="ops__kpi">
              <span class="ops__kpi-label">Throughput</span>
              <span class="ops__kpi-bar"><span class="ops__kpi-fill" style="width: 68%"></span></span>
            </div>
            <div class="ops__kpi">
              <span class="ops__kpi-label">On time</span>
              <span class="ops__kpi-bar"><span class="ops__kpi-fill" style="width: 84%"></span></span>
            </div>
            <div class="ops__kpi">
              <span class="ops__kpi-label">Exceptions</span>
              <span class="ops__kpi-bar">
                <span class="ops__kpi-fill ops__kpi-fill--warn" style="width: 22%"></span>
              </span>
            </div>
          </div>
          <span class="ops__note">Illustrative interface</span>
        </div>
      </div>
    </div>
  `,
  styles: `
    :host { display: block; }

    .ops {
      background: var(--c-pure);
      border: 1px solid var(--line);
      border-radius: 16px;
      box-shadow: var(--shadow-md);
      overflow: hidden;
      min-width: 0;
    }

    .ops__bar {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.6rem 0.9rem;
      background: var(--c-sand);
      border-bottom: 1px solid var(--line);
    }

    .ops__dot { width: 0.5rem; height: 0.5rem; border-radius: 50%; background: var(--c-accent); }

    .ops__title {
      font-family: var(--font-display);
      font-size: 0.72rem;
      font-weight: 600;
      letter-spacing: 0.06em;
      color: var(--c-ink-soft);
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .ops__body { display: grid; gap: 1rem; padding: 1rem; min-width: 0; }

    @media (min-width: 56rem) {
      .ops__body { grid-template-columns: minmax(0, 1.35fr) minmax(0, 1fr); align-items: start; }
    }

    /* The lane wraps on narrow screens rather than shrinking to unreadable. */
    .ops__lane {
      display: flex;
      flex-wrap: wrap;
      align-items: stretch;
      gap: 0.3rem;
      min-width: 0;
    }

    .ops__step {
      display: grid;
      gap: 0.15rem;
      flex: 1 1 5.5rem;
      min-width: 0;
      padding: 0.6rem 0.7rem;
      background: var(--c-sand);
      border: 1px solid var(--line);
      border-radius: var(--radius-sm);
    }

    .ops__step--done { background: var(--c-accent-soft); border-color: transparent; }
    .ops__step--now { background: var(--c-pure); border-color: var(--c-accent); }

    .ops__step-label {
      font-family: var(--font-display);
      font-size: 0.75rem;
      font-weight: 600;
      color: var(--c-ink);
    }

    .ops__step-meta { font-size: 0.68rem; color: var(--c-ink-muted); }

    .ops__arrow {
      align-self: center;
      flex: 0 0 0.7rem;
      height: 1px;
      background: var(--c-line-strong);
    }

    .ops__panel {
      display: grid;
      gap: 0.6rem;
      padding: 0.9rem;
      background: var(--c-sand);
      border: 1px solid var(--line);
      border-radius: var(--radius-sm);
      min-width: 0;
    }

    .ops__kpis { display: grid; gap: 0.5rem; }

    .ops__kpi { display: grid; gap: 0.25rem; min-width: 0; }

    .ops__kpi-label {
      font-size: 0.7rem;
      font-weight: 600;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: var(--c-ink-muted);
    }

    .ops__kpi-bar { display: block; height: 5px; border-radius: var(--radius-pill); background: var(--c-line); overflow: hidden; }

    .ops__kpi-fill { display: block; height: 100%; background: var(--c-accent); }
    .ops__kpi-fill--warn { background: var(--c-bronze); }

    .ops__note { font-size: 0.68rem; color: var(--c-ink-muted); }
  `,
})
export class SolutionsVisual {
  protected readonly stages = [
    { label: 'Request', meta: 'Captured once' },
    { label: 'Workflow', meta: 'Routed by rule' },
    { label: 'Operations', meta: 'Tracked live' },
    { label: 'People', meta: 'Assigned' },
    { label: 'Reporting', meta: 'One dataset' },
  ] as const;
}