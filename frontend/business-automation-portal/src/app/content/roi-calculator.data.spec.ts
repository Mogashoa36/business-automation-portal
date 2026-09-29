import { describe, expect, it } from 'vitest';

import {
  calculateRoi,
  formatCurrency,
  formatHours,
  hasMeaningfulInput,
  normaliseRoiInputs,
} from './roi-calculator.data';

/** Ten people, ten hours a week, fifty weeks, at R100 an hour, half automatable. */
const BASE = {
  employees: 10,
  hoursPerWeek: 10,
  hourlyCost: 100,
  weeksPerYear: 50,
  automationPercent: 50,
};

describe('calculateRoi', () => {
  it('multiplies people, hours and weeks for the current annual hours', () => {
    expect(calculateRoi(BASE).currentAnnualHours).toBe(5000);
  });

  it('applies the automation percentage to the automatable hours', () => {
    expect(calculateRoi(BASE).potentialAutomatedHours).toBe(2500);
  });

  it('values the recovered time at the supplied hourly cost', () => {
    // 2,500 recovered hours at R100 is R250,000.
    expect(calculateRoi(BASE).annualLabourValue).toBe(250000);
  });

  it('recovers nothing when nothing is automatable', () => {
    const result = calculateRoi({ ...BASE, automationPercent: 0 });
    expect(result.hoursRecovered).toBe(0);
    expect(result.annualLabourValue).toBe(0);
  });

  it('never recovers more hours than are currently spent', () => {
    const result = calculateRoi({ ...BASE, automationPercent: 100 });
    expect(result.hoursRecovered).toBe(result.currentAnnualHours);
  });

  it('handles a single person on a small process', () => {
    const result = calculateRoi({
      employees: 1,
      hoursPerWeek: 2,
      hourlyCost: 80,
      weeksPerYear: 45,
      automationPercent: 25,
    });
    expect(result.currentAnnualHours).toBe(90);
    expect(result.hoursRecovered).toBe(22.5);
    expect(result.annualLabourValue).toBe(1800);
  });
});

describe('normaliseRoiInputs', () => {
  it('clamps a percentage above 100', () => {
    const result = normaliseRoiInputs({ ...BASE, automationPercent: 250 });
    expect(result.automationPercent).toBe(100);
  });

  it('clamps weeks above a year', () => {
    expect(normaliseRoiInputs({ ...BASE, weeksPerYear: 400 }).weeksPerYear).toBe(52);
  });

  it('rejects a negative value', () => {
    expect(normaliseRoiInputs({ ...BASE, hoursPerWeek: -5 }).hoursPerWeek).toBe(0);
  });

  /** A blank or malformed field must not produce NaN. */
  it('falls back to the minimum for a non numeric value', () => {
    const result = normaliseRoiInputs({ ...BASE, employees: Number.NaN });
    expect(result.employees).toBe(1);
  });
});

describe('hasMeaningfulInput', () => {
  it('is true only when every figure is positive', () => {
    expect(hasMeaningfulInput(BASE)).toBe(true);
    expect(hasMeaningfulInput({ ...BASE, hourlyCost: 0 })).toBe(false);
    expect(hasMeaningfulInput({ ...BASE, employees: 0 })).toBe(false);
  });
});

describe('formatCurrency', () => {
  /**
   * South African English groups thousands with a space, not a comma. This is
   * deliberate and stable across server and browser, rather than depending on
   * whichever ICU data the runtime happens to ship.
   */
  it('groups thousands with a space, as South African English does', () => {
    expect(formatCurrency(250000)).toBe('R 250 000');
  });

  it('rounds to whole rand, because a time saving is not quoted in cents', () => {
    expect(formatCurrency(1234.6)).toBe('R 1 235');
  });

  it('renders zero rather than an empty value', () => {
    expect(formatCurrency(0)).toBe('R 0');
  });

  it('leaves a value under a thousand ungrouped', () => {
    expect(formatCurrency(999)).toBe('R 999');
  });
});

describe('formatHours', () => {
  it('rounds and groups', () => {
    expect(formatHours(5000)).toBe('5 000');
    expect(formatHours(1234.6)).toBe('1 235');
  });
});
