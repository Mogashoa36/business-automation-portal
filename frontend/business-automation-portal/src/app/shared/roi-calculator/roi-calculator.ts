import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PRIMARY_CTA } from '../../content/company';
import {
  RoiResult,
  calculateRoi,
  formatCurrency,
  formatHours,
  hasMeaningfulInput,
  normaliseRoiInputs,
} from '../../content/roi-calculator.data';
import { Icon } from '../icon/icon';
import { Reveal } from '../reveal/reveal';

/**
 * The Automation ROI Calculator.
 *
 * Everything is calculated in the browser as the visitor types. Nothing is
 * stored and nothing is transmitted: there is no call to the API, no cookie
 * and no analytics event carrying these numbers, because a person exploring
 * their own figures has not asked us to keep them.
 *
 * Every result is labelled as an estimate, and the wording avoids promising a
 * saving. The figure is the visitor's own arithmetic, not a quotation.
 */
@Component({
  selector: 'app-roi-calculator',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, Reveal],
  templateUrl: './roi-calculator.html',
  styleUrl: './roi-calculator.scss',
})
export class RoiCalculator {
  protected readonly ctaLabel = PRIMARY_CTA.label;
  protected readonly ctaLink = PRIMARY_CTA.link;

  protected readonly values = signal({
    employees: 3,
    hoursPerWeek: 5,
    hourlyCost: 150,
    weeksPerYear: 46,
    automationPercent: 50,
  });

  /** Null until there is enough input for a figure worth showing. */
  protected readonly result = computed<RoiResult | null>(() => {
    const inputs = normaliseRoiInputs(this.values());
    return hasMeaningfulInput(inputs) ? calculateRoi(inputs) : null;
  });

  protected readonly hours = formatHours;
  protected readonly currency = formatCurrency;

  /** Reads a numeric input, treating a blank or malformed value as zero. */
  protected update(field: keyof ReturnType<typeof defaultValues>, raw: string): void {
    const parsed = Number(raw);
    this.values.update((current) => ({ ...current, [field]: Number.isFinite(parsed) ? parsed : 0 }));
  }

  protected reset(): void {
    this.values.set(defaultValues());
  }
}

function defaultValues() {
  return { employees: 3, hoursPerWeek: 5, hourlyCost: 150, weeksPerYear: 46, automationPercent: 50 };
}