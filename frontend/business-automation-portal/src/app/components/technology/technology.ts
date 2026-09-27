import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { TECHNOLOGY_GROUPS } from '../../content/technology.data';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { SectionHeading } from '../../shared/section-heading/section-heading';

@Component({
  selector: 'app-technology',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, SectionHeading, CtaBanner],
  templateUrl: './technology.html',
  styleUrl: './technology.scss',
})
export class Technology {
  private readonly seo = inject(SeoService);
  protected readonly groups = TECHNOLOGY_GROUPS;

  constructor() {
    this.seo.update({
      title: 'Technology | Java, Spring Boot, Angular, PostgreSQL & AWS',
      description:
        'The technology behind our business software: Java and Spring Boot services, Angular and TypeScript interfaces, PostgreSQL, Oracle, MongoDB, AWS and CI/CD.',
      path: '/technology',
    });
  }
}
