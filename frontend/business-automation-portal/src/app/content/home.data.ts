import { IconName } from '../models/icon.model';

export interface OutcomePoint {
  readonly icon: IconName;
  readonly title: string;
  readonly description: string;
}

/**
 * The business outcomes the company sells, used by the "Why businesses choose
 * us" section. Deliberately written as outcomes rather than technology.
 */
export const WHY_CHOOSE_US: readonly OutcomePoint[] = [
  {
    icon: 'automation',
    title: 'Reduce manual work',
    description:
      'Repetitive administration is handled by the system, so your people spend their time on work that needs a person.',
  },
  {
    icon: 'trend-up',
    title: 'Improve operational efficiency',
    description:
      'Processes run to a defined sequence every time, instead of depending on who is available to chase the next step.',
  },
  {
    icon: 'layers',
    title: 'Centralise business information',
    description:
      'One reliable place for requests, approvals, tasks and records, instead of the same data copied across inboxes and spreadsheets.',
  },
  {
    icon: 'target',
    title: 'Improve visibility',
    description:
      'See what is open, what is late and who owns it, without asking three people and waiting for a reply.',
  },
  {
    icon: 'shield',
    title: 'Reduce errors',
    description:
      'Required fields, validation rules and controlled access mean mistakes are prevented rather than corrected downstream.',
  },
  {
    icon: 'reporting',
    title: 'Make better decisions',
    description:
      'Management works from current, consistent numbers rather than last week’s spreadsheet.',
  },
  {
    icon: 'spark',
    title: 'Build scalable digital solutions',
    description:
      'A solid foundation that handles today’s volume and the next three years of growth without being rebuilt.',
  },
];

/** The company promise shown under the hero. */
export const PROMISES: readonly string[] = [
  'Secure by design',
  'Built around your process',
  'Delivered on time',
  'Supported after go-live',
];
