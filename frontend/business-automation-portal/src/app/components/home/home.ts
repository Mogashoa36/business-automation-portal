import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { COMPANY } from '../../content/company';
import { PROMISES, WHY_CHOOSE_US } from '../../content/home.data';
import { SERVICES } from '../../content/services.data';
import { SOLUTIONS } from '../../content/solutions.data';
import { TECHNOLOGY_GROUPS } from '../../content/technology.data';
import { PortfolioProject } from '../../models/portfolio-project.model';
import { SeoService } from '../../services/seo.service';
import { PortfolioService } from '../../services/portfolio.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { SectionHeading } from '../../shared/section-heading/section-heading';

@Component({
  selector: 'app-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, SectionHeading, CtaBanner],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private readonly seo = inject(SeoService);
  private readonly portfolioService = inject(PortfolioService);

  protected readonly company = COMPANY;
  protected readonly services = SERVICES;
  protected readonly solutions = SOLUTIONS;
  protected readonly reasons = WHY_CHOOSE_US;
  protected readonly promises = PROMISES;
  protected readonly technology = TECHNOLOGY_GROUPS;

  /** Featured case studies shown near the bottom of the page. */
  protected readonly projects = signal<readonly PortfolioProject[]>([]);
  /** True when the bundled snapshot is shown because the API was unreachable. */
  protected readonly portfolioOffline = signal(false);

  constructor() {
    this.seo.update({
      title: 'Business Automation Company | Business Process Automation South Africa',
      description:
        'We automate manual business processes and build secure, scalable software that helps businesses work faster, smarter and more efficiently. Custom applications, portals and websites.',
      path: '/',
    });

    this.portfolioService.list().subscribe((result) => {
      const featured = result.projects.filter((project) => project.featured);
      this.projects.set((featured.length ? featured : result.projects).slice(0, 3));
      this.portfolioOffline.set(result.fromFallback);
    });
  }
}
