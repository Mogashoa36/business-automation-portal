import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SERVICES } from '../../../content/services.data';
import { SeoService } from '../../../services/seo.service';
import { ServiceDetail } from '../../../shared/service-detail/service-detail';

/** Primary service: business process automation. */
@Component({
  selector: 'app-business-process-automation',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ServiceDetail],
  template: `<app-service-detail [service]="service" />`,
})
export class BusinessProcessAutomation {
  private readonly seo = inject(SeoService);
  protected readonly service = SERVICES[0];

  constructor() {
    this.seo.update({ ...this.service.seo, path: '/services/business-process-automation' });
  }
}
