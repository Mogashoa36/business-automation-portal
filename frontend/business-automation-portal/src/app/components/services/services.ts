import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SERVICES } from '../../content/services.data';
import { SeoService } from '../../services/seo.service';
import { CtaBanner } from '../../shared/cta-banner/cta-banner';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { SectionHeading } from '../../shared/section-heading/section-heading';

@Component({
  selector: 'app-services',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, SectionHeading, CtaBanner],
  templateUrl: './services.html',
  styleUrl: './services.scss',
})
export class Services {
  private readonly seo = inject(SeoService);
  protected readonly services = SERVICES;

  constructor() {
    this.seo.update({
      title: 'Services | Business Automation, Custom Apps & Website Development',
      description:
        'Business process automation, custom business applications and website development. We replace manual processes with secure, scalable software.',
      path: '/services',
    });
  }
}
