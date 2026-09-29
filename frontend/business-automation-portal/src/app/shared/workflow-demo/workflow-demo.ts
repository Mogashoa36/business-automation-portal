import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import { WORKFLOW_STAGES } from '../../content/process-showcase.data';
import { Icon } from '../icon/icon';
import { Reveal } from '../reveal/reveal';

/**
 * An illustrative workflow a visitor can step through.
 *
 * Built as a proper tablist: arrow keys move between stages, Home and End jump
 * to the ends, and each panel is associated with its tab. That means the whole
 * demonstration is usable without a pointer.
 *
 * It is clearly labelled as illustrative. It shows the shape of an approval
 * workflow, not a live system and not a claim about a specific product.
 */
@Component({
  selector: 'app-workflow-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Icon, Reveal],
  templateUrl: './workflow-demo.html',
  styleUrl: './workflow-demo.scss',
})
export class WorkflowDemo {
  protected readonly stages = WORKFLOW_STAGES;
  protected readonly active = signal(0);

  protected readonly current = computed(() => this.stages[this.active()]);

  protected select(index: number): void {
    this.active.set(index);
  }

  /** Roving focus: arrows move between tabs and wrap at the ends. */
  protected onKeydown(event: KeyboardEvent, index: number): void {
    const last = this.stages.length - 1;
    let next: number | null = null;

    switch (event.key) {
      case 'ArrowRight':
      case 'ArrowDown':
        next = index === last ? 0 : index + 1;
        break;
      case 'ArrowLeft':
      case 'ArrowUp':
        next = index === 0 ? last : index - 1;
        break;
      case 'Home':
        next = 0;
        break;
      case 'End':
        next = last;
        break;
      default:
        return;
    }

    event.preventDefault();
    this.active.set(next);
    this.focusTab(next);
  }

  /** Moves focus with the selection, which is what a roving tabindex requires. */
  private focusTab(index: number): void {
    const host = document.getElementById('workflow-tabs');
    host?.querySelectorAll<HTMLButtonElement>('[role="tab"]')[index]?.focus();
  }
}