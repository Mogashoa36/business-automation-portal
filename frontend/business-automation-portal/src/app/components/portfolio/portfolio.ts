import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PortfolioProject } from '../../models/portfolio-project.model';
import { PortfolioService } from '../../services/portfolio.service';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { Reveal } from '../../shared/reveal/reveal';

@Component({
  selector: 'app-portfolio',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, CtaBanner, Reveal],
  templateUrl: './portfolio.html',
  styleUrl: './portfolio.scss',
})
export class Portfolio {
  private readonly seo = inject(SeoService);
  private readonly portfolioService = inject(PortfolioService);

  protected readonly projects = signal<readonly PortfolioProject[]>([]);
  protected readonly featured = signal<PortfolioProject | null>(null);
  protected readonly loading = signal(true);
  protected readonly fromFallback = signal(false);

  constructor() {
    this.seo.update({
      title: 'Portfolio | Automation & Custom Software Case Studies',
      description:
        'Selected automation and custom business software scenarios: the problem, the solution, the technologies used and the business results.',
      path: '/portfolio',
    });

    this.portfolioService.list().subscribe((result) => {
      this.featured.set(result.projects[0] ?? null);
      this.projects.set(result.projects.slice(1));
      this.fromFallback.set(result.fromFallback);
      this.loading.set(false);
    });
  }
}
