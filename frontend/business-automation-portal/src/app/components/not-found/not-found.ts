import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SERVICES } from '../../content/services.data';
import { SeoService } from '../../services/seo.service';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';

@Component({
  selector: 'app-not-found',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero],
  templateUrl: './not-found.html',
  styleUrl: './not-found.scss',
})
export class NotFound {
  private readonly seo = inject(SeoService);
  protected readonly services = SERVICES;

  constructor() {
    this.seo.update({
      title: 'Page not found | Business Automation Company',
      description: 'The page you were looking for could not be found.',
      path: '/404',
    });
  }
}
