import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SOLUTIONS } from '../../content/solutions.data';
import { SolutionContent } from '../../models/solution.model';
import { CtaBanner } from '../cta-banner/cta-banner';
import { Icon } from '../icon/icon';
import { PageHero } from '../page-hero/page-hero';
import { ProcessFlow } from '../process-flow/process-flow';
import { SectionHeading } from '../section-heading/section-heading';

/**
 * Presentation for a single solution. Each solution route renders this with
 * its own content so the five solution pages stay consistent.
 */
@Component({
  selector: 'app-solution-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, SectionHeading, ProcessFlow, CtaBanner],
  templateUrl: './solution-detail.html',
  styleUrl: './solution-detail.scss',
})
export class SolutionDetail {
  /** The solution to render. */
  readonly solution = input.required<SolutionContent>();

  protected readonly otherSolutions = computed(() => SOLUTIONS.filter((item) => item.slug !== this.solution().slug));
}
