import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SOLUTIONS } from '../../content/solutions.data';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { Reveal } from '../../shared/reveal/reveal';
import { SolutionsVisual } from '../../shared/solutions-visual/solutions-visual';

interface ChainStep {
  readonly title: string;
  readonly description: string;
}

@Component({
  selector: 'app-solutions',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, CtaBanner, Reveal, SolutionsVisual],
  templateUrl: './solutions.html',
  styleUrl: './solutions.scss',
})
export class Solutions {
  private readonly seo = inject(SeoService);
  protected readonly solutions = SOLUTIONS;

  /** How the solutions reinforce each other once more than one is in place. */
  protected readonly chain: readonly ChainStep[] = [
    {
      title: 'Workflows capture the data',
      description: 'Processes run once, on time, so the information operations relies on is never re-keyed.',
    },
    {
      title: 'Operations sees it live',
      description: 'Dashboards turn that data into a current view of what is happening right now.',
    },
    {
      title: 'Reporting explains it',
      description: 'Management gets the same information, summarised at the level they decide on.',
    },
    {
      title: 'Support processes follow',
      description: 'Employee and procurement workflows keep the surrounding processes in step.',
    },
    {
      title: 'It all runs on one foundation',
      description: 'A single secure, role based application underneath, rather than five disconnected tools.',
    },
  ];

  constructor() {
    this.seo.update({
      title: 'Solutions | Workflow, Operations, Employees, Procurement & Reporting',
      description:
        'Practical business software solutions: workflow management, operations management, employee management, procurement and reporting dashboards built for South African businesses.',
      path: '/solutions',
    });
  }
}
