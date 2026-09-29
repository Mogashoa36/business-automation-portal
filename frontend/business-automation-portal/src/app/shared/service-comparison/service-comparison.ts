import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SERVICE_COMPARISON } from '../../content/process-showcase.data';
import { Reveal } from '../reveal/reveal';

/**
 * The three services side by side.
 *
 * A real HTML table, because this is genuinely tabular information: a
 * screen reader user can head across a row to compare one criterion at a time,
 * which a stack of cards would not allow. The services are peers, so nothing is
 * ranked or scored.
 */
@Component({
  selector: 'app-service-comparison',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Reveal],
  templateUrl: './service-comparison.html',
  styleUrl: './service-comparison.scss',
})
export class ServiceComparison {
  protected readonly services = SERVICE_COMPARISON;
}