import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SOLUTIONS } from '../../content/solutions.data';
import { SolutionContent } from '../../models/solution.model';
import { CtaBanner } from '../cta-banner/cta-banner';
import { Icon } from '../icon/icon';
import { PageHero } from '../page-hero/page-hero';
import { ProcessFlow } from '../process-flow/process-flow';
import { Reveal } from '../reveal/reveal';

/**
 * Presentation for a single solution. Each solution route renders this with
 * its own content so the five solution pages stay consistent.
 */
@Component({
  selector: 'app-solution-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, ProcessFlow, CtaBanner, Reveal],
  templateUrl: './solution-detail.html',
  styleUrl: './solution-detail.scss',
})
export class SolutionDetail {
  /** The solution to render. */
  readonly solution = input.required<SolutionContent>();

  protected readonly otherSolutions = computed(() =>
    SOLUTIONS.filter((item) => item.slug !== this.solution().slug),
  );

  /** Zero padded index, e.g. 01, 02. */
  protected pad(value: number): string {
    return value.toString().padStart(2, '0');
  }
}
