import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SERVICES } from '../../content/services.data';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { FaqSection } from '../../shared/faq-section/faq-section';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { Reveal } from '../../shared/reveal/reveal';
import { ServiceComparison } from '../../shared/service-comparison/service-comparison';

interface ApproachStep {
  readonly title: string;
  readonly description: string;
}

@Component({
  selector: 'app-services',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, CtaBanner, Reveal, ServiceComparison, FaqSection],
  templateUrl: './services.html',
  styleUrl: './services.scss',
})
export class Services {
  private readonly seo = inject(SeoService);
  protected readonly services = SERVICES;

  /** How an engagement actually runs, from discovery to support. */
  protected readonly approach: readonly ApproachStep[] = [
    {
      title: 'Understand the process',
      description: 'We map how the work is actually done today, including the workarounds nobody has written down.',
    },
    {
      title: 'Identify what to automate',
      description: 'We separate the steps that genuinely need automation from the ones that only need better information.',
    },
    {
      title: 'Build it properly',
      description: 'Secure, tested software with the access controls, audit trails and reporting the business requires.',
    },
    {
      title: 'Support it long term',
      description: 'Maintenance, changes and improvements after go-live, because business software never really finishes.',
    },
  ];

  constructor() {
    this.seo.update({
      title: 'Services | Business Automation, Custom Apps & Website Development',
      description:
        'Business process automation, custom business applications and website development. We replace manual processes with secure, scalable software.',
      path: '/services',
    });
  }
}
