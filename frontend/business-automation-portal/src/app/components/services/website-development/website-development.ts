import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SERVICES } from '../../../content/services.data';
import { SeoService } from '../../../services/seo.service';
import { ServiceDetail } from '../../../shared/service-detail/service-detail';

/** Supporting service: professional website development. */
@Component({
  selector: 'app-website-development',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ServiceDetail],
  template: `<app-service-detail [service]="service" />`,
})
export class WebsiteDevelopment {
  private readonly seo = inject(SeoService);
  protected readonly service = SERVICES[2];

  constructor() {
    this.seo.update({ ...this.service.seo, path: '/services/website-development' });
  }
}
