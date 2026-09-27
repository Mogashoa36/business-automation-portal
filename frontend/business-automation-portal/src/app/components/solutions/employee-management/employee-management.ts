import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SOLUTIONS } from '../../../content/solutions.data';
import { SeoService } from '../../../services/seo.service';
import { SolutionDetail } from '../../../shared/solution-detail/solution-detail';

@Component({
  selector: 'app-employee-management',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SolutionDetail],
  template: `<app-solution-detail [solution]="solution" />`,
})
export class EmployeeManagement {
  private readonly seo = inject(SeoService);
  protected readonly solution = SOLUTIONS[2];

  constructor() {
    this.seo.update({ ...this.solution.seo, path: '/solutions/employee-management' });
  }
}
