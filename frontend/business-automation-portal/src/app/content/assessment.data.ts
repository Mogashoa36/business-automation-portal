/**
 * The Automation Assessment.
 *
 * Six questions, each option carrying a weight. The score is a weighted total
 * and the opportunities come from which answers were selected.
 *
 * Honesty note: this is an indicative guide, not a quote and not a promise. It
 * deliberately makes no claim about a saving, a payback period or a guaranteed
 * outcome, because a short form cannot honestly produce one. The copy says
 * "possible" and "typically", and the result always ends in a conversation
 * rather than a number to bank.
 */

/** A single answer option within a question. */
export interface AssessmentOption {
  readonly label: string;
  /** Relative contribution to the score. */
  readonly weight: number;
  /** Opportunity tags this answer suggests. */
  readonly signals: readonly string[];
}

export interface AssessmentQuestion {
  readonly id: string;
  readonly question: string;
  readonly options: readonly AssessmentOption[];
}

/** One suggested opportunity. */
export interface Opportunity {
  readonly title: string;
  readonly description: string;
  readonly strength: 'strong' | 'possible';
}

/** The result of an assessment. */
export interface AssessmentResult {
  /** 0-100. Deliberately a band, not a precise-looking score. */
  readonly score: number;
  readonly band: 'low' | 'moderate' | 'high';
  readonly bandLabel: string;
  readonly bandSummary: string;
  readonly opportunities: readonly Opportunity[];
}

export const ASSESSMENT_QUESTIONS: readonly AssessmentQuestion[] = [
  {
    id: 'process',
    question: 'How is this process run today?',
    options: [
      {
        label: 'Mostly by hand, across spreadsheets and email',
        weight: 3,
        signals: ['digital-workflows', 'centralised-records'],
      },
      {
        label: 'A mix of manual steps and a few existing tools',
        weight: 2,
        signals: ['digital-workflows', 'centralised-records'],
      },
      { label: 'Mostly in a system already', weight: 1, signals: ['integrations'] },
      {
        label: 'Not consistently - it depends who is doing it',
        weight: 3,
        signals: ['digital-workflows'],
      },
    ],
  },
  {
    id: 'frequency',
    question: 'How often does it run?',
    options: [
      { label: 'Several times a day', weight: 3, signals: ['automation', 'notifications'] },
      { label: 'Daily', weight: 3, signals: ['automation', 'notifications'] },
      { label: 'Weekly', weight: 2, signals: ['automation', 'reporting'] },
      { label: 'Monthly or less', weight: 1, signals: ['reporting'] },
    ],
  },
  {
    id: 'approvals',
    question: 'Are there approvals or sign-offs in the process?',
    options: [
      { label: 'Yes, several people approve at different stages', weight: 3, signals: ['approval-routing'] },
      { label: 'Yes, a single approval', weight: 2, signals: ['approval-routing'] },
      { label: 'Occasionally', weight: 1, signals: ['notifications'] },
      { label: 'No approvals', weight: 0, signals: [] },
    ],
  },
  {
    id: 'dataEntry',
    question: 'Is the same information typed in more than once?',
    options: [
      { label: 'Yes, repeatedly, into different systems', weight: 3, signals: ['centralised-records'] },
      { label: 'Sometimes, mainly copying between systems', weight: 2, signals: ['centralised-records'] },
      { label: 'Rarely', weight: 1, signals: [] },
      { label: 'Never', weight: 0, signals: [] },
    ],
  },
  {
    id: 'reporting',
    question: 'Is a report or summary produced from it?',
    options: [
      { label: 'Yes, compiled by hand every cycle', weight: 3, signals: ['reporting-dashboards'] },
      { label: 'Yes, from a spreadsheet someone maintains', weight: 2, signals: ['reporting-dashboards'] },
      { label: 'Only occasionally', weight: 1, signals: ['reporting-dashboards'] },
      { label: 'No', weight: 0, signals: [] },
    ],
  },
  {
    id: 'followUp',
    question: 'Does someone chase it manually to keep it moving?',
    options: [
      { label: 'Yes, most items need chasing', weight: 3, signals: ['notifications', 'approval-routing'] },
      { label: 'Sometimes, when things slip', weight: 2, signals: ['notifications'] },
      { label: 'Rarely, it moves on its own', weight: 0, signals: [] },
      { label: 'There is no follow-up needed', weight: 0, signals: [] },
    ],
  },
];

/** Maps a signal to the opportunity it justifies. */
const OPPORTUNITIES: Record<string, Omit<Opportunity, 'strength'>> = {
  'digital-workflows': {
    title: 'Digital workflows',
    description:
      'The manual steps become a tracked process with a status for every item, rather than a chain of emails and spreadsheets.',
  },
  notifications: {
    title: 'Automated notifications',
    description:
      'The right person is told about the next action automatically, instead of a request sitting until someone remembers it.',
  },
  'approval-routing': {
    title: 'Approval routing',
    description:
      'Approvals move to the correct person automatically, with the decision history kept on the request itself.',
  },
  'centralised-records': {
    title: 'Centralised records',
    description:
      'One record per request or item, entered once, so the same information is not retyped into several places.',
  },
  'reporting-dashboards': {
    title: 'Reporting and dashboards',
    description:
      'Reports come from the same data the process runs on, so a summary is a view rather than a reporting task.',
  },
  integrations: {
    title: 'Integration with existing systems',
    description: 'Existing tools are connected where that makes sense, rather than replaced without a reason.',
  },
  automation: {
    title: 'Rules-based automation',
    description: 'Routine steps run on a defined rule, so the process keeps moving without manual effort.',
  },
};

/** A signal needs at least this many answers before it is called "strong". */
const STRONG_SIGNAL_COUNT = 2;

const BAND_LABELS: Record<AssessmentResult['band'], string> = {
  low: 'Some automation potential',
  moderate: 'Clear automation potential',
  high: 'Strong automation potential',
};

const BAND_SUMMARIES: Record<AssessmentResult['band'], string> = {
  low: 'There may be a few steps worth digitising, though the process is not heavily manual today.',
  moderate:
    'There is a real opportunity here. Several steps are manual and repeated often enough to be worth changing.',
  high:
    'This looks like a strong candidate for automation: it is manual, frequent, and spread across more than one system.',
};

/**
 * Scores a set of answers.
 *
 * Exported separately from the component so the logic can be unit tested
 * without rendering anything.
 *
 * @param answers the selected option label, keyed by question id
 */
export function scoreAssessment(answers: Readonly<Record<string, string>>): AssessmentResult {
  const counts = new Map<string, number>();
  let earned = 0;
  let available = 0;

  for (const question of ASSESSMENT_QUESTIONS) {
    const chosen = question.options.find((option) => option.label === answers[question.id]);
    // An unanswered question contributes nothing rather than a neutral value,
    // so a partial result is never flattered.
    if (!chosen) {
      continue;
    }
    earned += chosen.weight;
    available += maxWeight(question);
    for (const signal of chosen.signals) {
      counts.set(signal, (counts.get(signal) ?? 0) + 1);
    }
  }

  // Normalised against the best achievable score, so the band reflects the
  // strongest possible process rather than an arbitrary total.
  const score = available === 0 ? 0 : Math.round((earned / available) * 100);
  const band: AssessmentResult['band'] = score >= 66 ? 'high' : score >= 33 ? 'moderate' : 'low';

  const opportunities: Opportunity[] = [...counts.entries()]
    .filter(([signal]) => Boolean(OPPORTUNITIES[signal]))
    .sort((a, b) => b[1] - a[1])
    .map(([signal, count]) => ({
      ...OPPORTUNITIES[signal],
      strength: count >= STRONG_SIGNAL_COUNT ? 'strong' : 'possible',
    }));

  return {
    score,
    band,
    bandLabel: BAND_LABELS[band],
    bandSummary: BAND_SUMMARIES[band],
    opportunities,
  };
}

/** The highest weight any option in a question offers. */
function maxWeight(question: AssessmentQuestion): number {
  return Math.max(...question.options.map((option) => option.weight));
}
