import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { distinctUntilChanged, map, switchMap, tap } from 'rxjs';

import { PortfolioProject } from '../../../models/portfolio-project.model';
import { PortfolioService } from '../../../services/portfolio.service';
import { SeoService } from '../../../services/seo.service';
import { CtaBanner } from '../../../shared/cta-banner/cta-banner';
import { Icon } from '../../../shared/icon/icon';
import { PageHero } from '../../../shared/page-hero/page-hero';
import { ResponsiveImage } from '../../../shared/responsive-image/responsive-image';
import { Reveal } from '../../../shared/reveal/reveal';

@Component({
  selector: 'app-portfolio-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, CtaBanner, Reveal, ResponsiveImage],
  templateUrl: './portfolio-detail.html',
  styleUrl: './portfolio-detail.scss',
})
export class PortfolioDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly portfolioService = inject(PortfolioService);
  private readonly seo = inject(SeoService);

  /** `undefined` while loading, `null` when the slug does not exist. */
  protected readonly project = signal<PortfolioProject | null | undefined>(undefined);

  constructor() {
    this.route.paramMap
      .pipe(
        map((params) => params.get('slug') ?? ''),
        distinctUntilChanged(),
        tap(() => this.project.set(undefined)),
        switchMap((slug) => this.portfolioService.bySlug(slug)),
      )
      .subscribe((project) => {
        this.project.set(project);
        this.applySeo(project);
      });
  }

  private applySeo(project: PortfolioProject | null): void {
    if (!project) {
      this.seo.update({
        title: 'Case study not found | Bizflow Technologies',
        description: 'The requested case study could not be found.',
        path: '/portfolio',
      });
      return;
    }

    this.seo.update({
      title: `${project.name} | Case Study`,
      description: project.summary,
      path: `/portfolio/${project.slug}`,
      type: 'article',
    });
  }
}
