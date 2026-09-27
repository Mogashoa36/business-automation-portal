import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SOLUTIONS } from '../../content/solutions.data';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { SectionHeading } from '../../shared/section-heading/section-heading';

@Component({
  selector: 'app-solutions',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, SectionHeading, CtaBanner],
  templateUrl: './solutions.html',
  styleUrl: './solutions.scss',
})
export class Solutions {
  private readonly seo = inject(SeoService);
  protected readonly solutions = SOLUTIONS;

  constructor() {
    this.seo.update({
      title: 'Solutions | Workflow, Operations, Employees, Procurement & Reporting',
      description:
        'Practical business software solutions: workflow management, operations management, employee management, procurement and reporting dashboards built for South African businesses.',
      path: '/solutions',
    });
  }
}
