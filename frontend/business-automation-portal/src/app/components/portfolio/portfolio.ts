import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PortfolioProject } from '../../models/portfolio-project.model';
import { PortfolioService } from '../../services/portfolio.service';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { ResponsiveImage } from '../../shared/responsive-image/responsive-image';
import { Reveal } from '../../shared/reveal/reveal';

/**
 * The portfolio, with a category filter.
 *
 * The filter is a plain list of buttons, not a custom widget: each is a single
 * tab stop and the pressed state is announced. Filtering happens in memory over
 * the projects already loaded, so selecting a category is instant and costs no
 * extra request.
 *
 * The lead case study sits outside the filter, so there is always a strong
 * piece at the top of the page.
 */
@Component({
  selector: 'app-portfolio',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, CtaBanner, Reveal, ResponsiveImage],
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

  /** The active filter. `null` means "everything". */
  protected readonly activeCategory = signal<string | null>(null);

  /** Only categories that actually have a case study, so no filter is ever empty. */
  protected readonly categories = signal<readonly string[]>([]);

  /** The grid, after the active filter is applied. */
  protected readonly visible = signal<readonly PortfolioProject[]>([]);

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
      this.categories.set(distinctCategories(result.projects));
      this.reapply();
      this.fromFallback.set(result.fromFallback);
      this.loading.set(false);
    });
  }

  protected filter(category: string | null): void {
    this.activeCategory.set(category);
    this.reapply();
  }

  protected countFor(category: string | null): number {
    if (category === null) {
      return this.projects().length;
    }
    return this.projects().filter((project) => project.category === category).length;
  }

  private reapply(): void {
    const category = this.activeCategory();
    this.visible.set(
      category === null ? this.projects() : this.projects().filter((project) => project.category === category),
    );
  }
}

/** The distinct, sorted categories present in the loaded projects. */
function distinctCategories(projects: readonly PortfolioProject[]): readonly string[] {
  const categories = new Set<string>();
  for (const project of projects) {
    // Null is skipped, so an uncategorised project never produces an unusable
    // filter chip.
    if (project.category) {
      categories.add(project.category);
    }
  }
  return [...categories].sort((a, b) => a.localeCompare(b));
}