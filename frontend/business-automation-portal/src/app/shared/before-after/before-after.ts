import { ChangeDetectionStrategy, Component } from '@angular/core';

import { AFTER_STEPS, BEFORE_STEPS, PROCESS_PAIRS } from '../../content/process-showcase.data';
import { Reveal } from '../reveal/reveal';

/**
 * Before and after, shown as two flows and as a paired list.
 *
 * Motion is transform and opacity only, and the reveal directive already
 * suppresses it under `prefers-reduced-motion`. Nothing loops: the comparison
 * settles once and holds still, which keeps it reading as a diagram rather than
 * an animation.
 */
@Component({
  selector: 'app-before-after',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Reveal],
  templateUrl: './before-after.html',
  styleUrl: './before-after.scss',
})
export class BeforeAfter {
  protected readonly before = BEFORE_STEPS;
  protected readonly after = AFTER_STEPS;
  protected readonly pairs = PROCESS_PAIRS;
}