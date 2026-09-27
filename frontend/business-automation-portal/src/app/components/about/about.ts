import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { COMPANY } from '../../content/company';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { SectionHeading } from '../../shared/section-heading/section-heading';

interface ValuePoint {
  readonly icon: 'spark' | 'shield' | 'lock' | 'check' | 'target' | 'document' | 'trend-up' | 'layers';
  readonly title: string;
  readonly description: string;
}

@Component({
  selector: 'app-about',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, SectionHeading, CtaBanner],
  templateUrl: './about.html',
  styleUrl: './about.scss',
})
export class About {
  private readonly seo = inject(SeoService);
  protected readonly company = COMPANY;

  protected readonly values: readonly ValuePoint[] = [
    {
      icon: 'spark',
      title: 'Innovation',
      description:
        'We look for the simplest change that actually removes the manual work, not the most impressive technology.',
    },
    {
      icon: 'shield',
      title: 'Reliability',
      description:
        'Business software has to work when people depend on it. We build for boring dependability and test it properly.',
    },
    {
      icon: 'lock',
      title: 'Security',
      description:
        'Authentication, role based access and audit trails are part of the design from the first day, not an add-on later.',
    },
    {
      icon: 'check',
      title: 'Quality',
      description:
        'We would rather deliver one process properly than half-finish five. Work is checked before it reaches your team.',
    },
    {
      icon: 'document',
      title: 'Transparency',
      description:
        'You see what is being built, what it costs and where the risks are. No surprises part way through a project.',
    },
    {
      icon: 'target',
      title: 'Customer-focused delivery',
      description:
        'The measure of the work is whether your people use it and your business runs better, not how much code was written.',
    },
    {
      icon: 'trend-up',
      title: 'Continuous improvement',
      description:
        'Once a process is digital it can be measured. We use that to keep removing the next layer of friction.',
    },
  ];

  protected readonly approach: readonly { title: string; description: string }[] = [
    {
      title: 'We ask before we build',
      description:
        'Understanding the real process takes time, and it is the part that decides whether the software actually helps.',
    },
    {
      title: 'We automate what should be automated',
      description:
        'Not every task needs software. We are as happy to remove a step entirely as to automate it.',
    },
    {
      title: 'We build for the people using it',
      description:
        'Interfaces are designed around real users on real devices, including the ones on a factory floor.',
    },
    {
      title: 'We stay accountable',
      description:
        'Support after go-live is part of the job. Software that nobody maintains quietly stops working.',
    },
  ];

  constructor() {
    this.seo.update({
      title: 'About | Business Automation Company',
      description:
        'We help businesses use technology to simplify operations, automate repetitive processes and build scalable digital solutions. Our mission, vision and values.',
      path: '/about',
    });
  }
}
