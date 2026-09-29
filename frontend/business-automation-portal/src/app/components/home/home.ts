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
import { AutomationAssessment } from '../../shared/automation-assessment/automation-assessment';
import { BeforeAfter } from '../../shared/before-after/before-after';
import { DeliveryTimeline } from '../../shared/delivery-timeline/delivery-timeline';
import { DeviceTrio } from '../../shared/device-trio/device-trio';
import { Icon } from '../../shared/icon/icon';
import { ProcessFlow } from '../../shared/process-flow/process-flow';
import { Reveal } from '../../shared/reveal/reveal';
import { RoiCalculator } from '../../shared/roi-calculator/roi-calculator';
import { WorkflowDemo } from '../../shared/workflow-demo/workflow-demo';

@Component({
  selector: 'app-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterLink,
    Icon,
    ProcessFlow,
    CtaBanner,
    Reveal,
    BeforeAfter,
    AutomationAssessment,
    RoiCalculator,
    WorkflowDemo,
    DeliveryTimeline,
    DeviceTrio,
  ],
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

  /** The primary service, given the strongest emphasis on the page. */
  protected readonly primary = SERVICES[0];

  protected readonly projects = signal<readonly PortfolioProject[]>([]);
  protected readonly featured = signal<PortfolioProject | null>(null);
  /** True when the bundled snapshot is shown because the API was unreachable. */
  protected readonly portfolioOffline = signal(false);

  constructor() {
    this.seo.update({
      title: 'Bizflow Technologies | Business Process Automation South Africa',
      description:
        'Bizflow Technologies builds software that automates business processes, simplifies operations, and turns manual work into efficient digital workflows. Custom applications, portals and websites.',
      path: '/',
    });

    this.portfolioService.list().subscribe((result) => {
      const featured = result.projects.filter((project) => project.featured);
      const ordered = featured.length ? featured : result.projects;
      this.featured.set(ordered[0] ?? null);
      this.projects.set(ordered.slice(1, 4));
      this.portfolioOffline.set(result.fromFallback);
    });
  }
}
