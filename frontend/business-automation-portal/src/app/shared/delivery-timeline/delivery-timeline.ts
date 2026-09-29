import { ChangeDetectionStrategy, Component } from '@angular/core';

import { DELIVERY_STAGES } from '../../content/process-showcase.data';
import { Reveal } from '../reveal/reveal';

/**
 * The seven stage delivery timeline.
 *
 * A vertical rail on narrow screens and a wrapping row on wide ones. It reveals
 * once and holds still, so it reads as a process rather than a carousel, and the
 * reveal directive already honours `prefers-reduced-motion`.
 */
@Component({
  selector: 'app-delivery-timeline',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Reveal],
  templateUrl: './delivery-timeline.html',
  styleUrl: './delivery-timeline.scss',
})
export class DeliveryTimeline {
  protected readonly stages = DELIVERY_STAGES;
}