import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { COMPANY } from '../../content/company';
import { SERVICES } from '../../content/services.data';
import { SOLUTIONS } from '../../content/solutions.data';
import { Icon } from '../../shared/icon/icon';

@Component({
  selector: 'app-footer',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon],
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
})
export class Footer {
  protected readonly company = COMPANY;
  protected readonly services = SERVICES;
  protected readonly solutions = SOLUTIONS;
  protected readonly year = new Date().getFullYear();
  /** Phone number in a `tel:` friendly format (no spaces). */
  protected readonly dialNumber = COMPANY.phone.replace(/\s/g, '');
}
