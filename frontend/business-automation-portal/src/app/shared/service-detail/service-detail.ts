import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SERVICES } from '../../content/services.data';
import { ServiceContent } from '../../models/service.model';
import { CtaBanner } from '../cta-banner/cta-banner';
import { Icon } from '../icon/icon';
import { PageHero } from '../page-hero/page-hero';
import { ProcessFlow } from '../process-flow/process-flow';
import { Reveal } from '../reveal/reveal';

/**
 * Presentation for a single service. Each service route renders this with its
 * own content, so the three service pages stay consistent without duplicating
 * the same template three times.
 */
@Component({
  selector: 'app-service-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, PageHero, ProcessFlow, CtaBanner, Reveal],
  templateUrl: './service-detail.html',
  styleUrl: './service-detail.scss',
})
export class ServiceDetail {
  /** The service to render. */
  readonly service = input.required<ServiceContent>();

  protected readonly otherServices = computed(() => SERVICES.filter((item) => item.slug !== this.service().slug));

  /** Zero padded index, e.g. 01, 02. */
  protected pad(value: number): string {
    return value.toString().padStart(2, '0');
  }
}
