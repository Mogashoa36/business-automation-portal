import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SERVICES } from '../../../content/services.data';
import { SeoService } from '../../../services/seo.service';
import { ServiceDetail } from '../../../shared/service-detail/service-detail';

/** Core delivery capability: custom business applications. */
@Component({
  selector: 'app-custom-business-applications',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ServiceDetail],
  template: `<app-service-detail [service]="service" />`,
})
export class CustomBusinessApplications {
  private readonly seo = inject(SeoService);
  protected readonly service = SERVICES[1];

  constructor() {
    this.seo.update({ ...this.service.seo, path: '/services/custom-business-applications' });
  }
}
