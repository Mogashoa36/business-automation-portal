/**
 * The Automation ROI Calculator.
 *
 * Pure arithmetic, kept out of the component so it can be unit tested and so
 * there is exactly one definition of what each figure means.
 *
 * The result is always an ESTIMATE and the UI says so. No figure here is a
 * promise, a quote or a guaranteed saving: it is the arithmetic a visitor can
 * do themselves with their own numbers, and the wording deliberately reflects
 * that. Nothing is stored or transmitted - the calculation happens entirely in
 * the browser and no value is ever sent anywhere.
 */

export interface RoiInputs {
  /** People involved in the process. */
  readonly employees: number;
  /** Hours each person spends on it per week. */
  readonly hoursPerWeek: number;
  /** Average cost of one hour of their time, in rand. */
  readonly hourlyCost: number;
  /** Working weeks in a year. */
  readonly weeksPerYear: number;
  /** Share of the time that could plausibly be automated, as a percentage. */
  readonly automationPercent: number;
}

export interface RoiResult {
  readonly currentAnnualHours: number;
  readonly potentialAutomatedHours: number;
  readonly hoursRecovered: number;
  /** Estimated annual labour value of the recovered time, in rand. */
  readonly annualLabourValue: number;
  /** Effective hourly cost implied by the inputs, useful when a figure looks off. */
  readonly effectiveHourlyCost: number;
}

/** Sensible bounds, applied so a stray value cannot produce nonsense. */
export const ROI_LIMITS = {
  employees: { min: 1, max: 10000 },
  hoursPerWeek: { min: 0, max: 100 },
  hourlyCost: { min: 0, max: 100000 },
  weeksPerYear: { min: 1, max: 52 },
  automationPercent: { min: 0, max: 100 },
} as const;

/**
 * Groups digits in threes with a space separator.
 *
 * Written by hand rather than through `toLocaleString`, for two reasons. South
 * African English groups with a space rather than a comma, and ICU data differs
 * between the server and the browser, which would make the server rendered HTML
 * disagree with the hydrated page. Doing it explicitly guarantees the same
 * output everywhere.
 */
function group(value: number): string {
  const rounded = Math.round(value);
  // Uses a plain space so the result is stable and easy to assert on.
  return rounded.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
}

/** South African rand. Whole rand only, which is how a time saving is quoted. */
export function formatCurrency(value: number): string {
  return `R ${group(value)}`;
}

/** Whole hours, grouped, because a decimal here adds false precision. */
export function formatHours(value: number): string {
  return group(value);
}

/** Clamps a value into its allowed range. */
function clamp(value: number, min: number, max: number): number {
  if (!Number.isFinite(value)) {
    return min;
  }
  return Math.min(Math.max(value, min), max);
}

/** Normalises raw form values, which arrive as strings, into safe numbers. */
export function normaliseRoiInputs(raw: Readonly<Record<keyof RoiInputs, string | number>>): RoiInputs {
  return {
    employees: clamp(Number(raw.employees), ROI_LIMITS.employees.min, ROI_LIMITS.employees.max),
    hoursPerWeek: clamp(Number(raw.hoursPerWeek), ROI_LIMITS.hoursPerWeek.min, ROI_LIMITS.hoursPerWeek.max),
    hourlyCost: clamp(Number(raw.hourlyCost), ROI_LIMITS.hourlyCost.min, ROI_LIMITS.hourlyCost.max),
    weeksPerYear: clamp(Number(raw.weeksPerYear), ROI_LIMITS.weeksPerYear.min, ROI_LIMITS.weeksPerYear.max),
    automationPercent: clamp(
      Number(raw.automationPercent),
      ROI_LIMITS.automationPercent.min,
      ROI_LIMITS.automationPercent.max,
    ),
  };
}

/**
 * Works out the estimate.
 *
 * `hoursRecovered` is the automatable portion only, so it can never exceed the
 * hours currently being spent.
 */
export function calculateRoi(inputs: RoiInputs): RoiResult {
  const safe = normaliseRoiInputs(inputs);

  const currentAnnualHours = safe.employees * safe.hoursPerWeek * safe.weeksPerYear;
  const potentialAutomatedHours = currentAnnualHours * (safe.automationPercent / 100);
  const hoursRecovered = potentialAutomatedHours;

  return {
    currentAnnualHours,
    potentialAutomatedHours,
    hoursRecovered,
    annualLabourValue: hoursRecovered * safe.hourlyCost,
    effectiveHourlyCost: safe.hourlyCost,
  };
}

/** True when there is enough input for a figure worth showing. */
export function hasMeaningfulInput(inputs: RoiInputs): boolean {
  return (
    inputs.employees > 0 &&
    inputs.hoursPerWeek > 0 &&
    inputs.weeksPerYear > 0 &&
    inputs.hourlyCost > 0 &&
    inputs.automationPercent > 0
  );
}
