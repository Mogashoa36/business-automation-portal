import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SOLUTIONS } from '../../../content/solutions.data';
import { SeoService } from '../../../services/seo.service';
import { SolutionDetail } from '../../../shared/solution-detail/solution-detail';

@Component({
  selector: 'app-operations-management',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SolutionDetail],
  template: `<app-solution-detail [solution]="solution" />`,
})
export class OperationsManagement {
  private readonly seo = inject(SeoService);
  protected readonly solution = SOLUTIONS[1];

  constructor() {
    this.seo.update({ ...this.solution.seo, path: '/solutions/operations-management' });
  }
}
