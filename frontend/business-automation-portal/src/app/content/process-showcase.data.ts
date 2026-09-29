/**
 * The Before / After process showcase, the delivery timeline, the interactive
 * workflow demonstration and the service comparison.
 *
 * All static marketing content, so it lives in the application rather than the
 * database. Nothing here is invented: the steps describe the shape of work the
 * company actually does, and the workflow demonstration is explicitly labelled
 * as illustrative.
 */

/** The manual process a visitor is likely to recognise. */
export const BEFORE_STEPS: readonly string[] = [
  'Email',
  'Spreadsheet',
  'Manual approval',
  'Follow-up',
  'Manual report',
];

/** The same work once it is a digital workflow. */
export const AFTER_STEPS: readonly string[] = [
  'Digital request',
  'Automated workflow',
  'Approval routing',
  'Notifications',
  'Dashboard',
];

/** One step of a before/after pairing. */
export interface ProcessPair {
  readonly before: string;
  readonly after: string;
  /** What actually changes, in one line. */
  readonly difference: string;
}

export const PROCESS_PAIRS: readonly ProcessPair[] = [
  {
    before: 'A request arrives by email and gets read twice.',
    after: 'One digital request, entered once.',
    difference: 'The request stops being retyped and re-read.',
  },
  {
    before: 'Progress is tracked in a shared spreadsheet.',
    after: 'The workflow holds the status of every item.',
    difference: 'There is one current answer to "where is this?".',
  },
  {
    before: 'Approvals are chased, often more than once.',
    after: 'Approval routing moves it to the right person.',
    difference: 'Waiting becomes visible instead of silent.',
  },
  {
    before: 'Someone has to remember to follow up.',
    after: 'Notifications fire at the right moment.',
    difference: 'Nothing stalls quietly in an inbox.',
  },
  {
    before: 'Reporting is compiled by hand each cycle.',
    after: 'Reporting comes from the same data.',
    difference: 'A report becomes a view rather than a task.',
  },
];

/** The seven delivery stages, in order. */
export interface DeliveryStage {
  readonly title: string;
  readonly summary: string;
  readonly detail: string;
}

export const DELIVERY_STAGES: readonly DeliveryStage[] = [
  {
    title: 'Discover',
    summary: 'We look at the process as it actually runs.',
    detail:
      'We map who does what, where the data lives today, and which steps cause the most waiting or rework.',
  },
  {
    title: 'Plan',
    summary: 'We agree what to build, and what to leave alone.',
    detail:
      'Scope, sequence and the things explicitly out of scope. You get a clear view of what is changing before anything is built.',
  },
  {
    title: 'Design',
    summary: 'The workflow and screens are designed together.',
    detail:
      'Steps, rules, roles and states are agreed up front, so the build is not a series of surprises.',
  },
  {
    title: 'Build',
    summary: 'We build in short, visible increments.',
    detail:
      'You see working software regularly rather than waiting months for a first reveal, and can steer while it is cheap to change.',
  },
  {
    title: 'Test',
    summary: 'We prove it works before it reaches your team.',
    detail:
      'Functional, permission and data checks against the agreed rules, with your team walking the real scenarios.',
  },
  {
    title: 'Deploy',
    summary: 'A careful release, with a way back.',
    detail:
      'We release when it is ready, with monitoring in place and a rollback if something unexpected shows up.',
  },
  {
    title: 'Support',
    summary: 'We stay reachable after go-live.',
    detail:
      'Fixes, small changes and advice as the process settles. A system nobody can get help with is not finished.',
  },
];

/** One stage of the illustrative workflow. */
export interface WorkflowStage {
  readonly name: string;
  readonly summary: string;
  readonly detail: string;
  /** What the system is doing at this point, as a short list. */
  readonly actions: readonly string[];
}

export const WORKFLOW_STAGES: readonly WorkflowStage[] = [
  {
    name: 'Request',
    summary: 'Someone raises the request in a form instead of an email.',
    detail:
      'The requester fills in a short form, so the information arrives complete and in the same shape every time.',
    actions: ['Details captured once', 'Reference number issued', 'Requester notified'],
  },
  {
    name: 'Review',
    summary: 'The request is checked and, if needed, sent back for more detail.',
    detail:
      'An incomplete or unclear request goes back to the requester with a clear reason, rather than being guessed at.',
    actions: ['Completeness check', 'Clarification request if needed', 'Status set to in review'],
  },
  {
    name: 'Approval',
    summary: 'The request is routed to whoever is allowed to approve it.',
    detail:
      'Approval rules decide who sees it and in what order, and every decision is recorded against the request.',
    actions: ['Routed by rule', 'Reminder if it stalls', 'Decision recorded'],
  },
  {
    name: 'Processing',
    summary: 'The approved work is carried out and tracked to completion.',
    detail:
      'The work is assigned with a status, so it is clear what is outstanding and who holds it.',
    actions: ['Assigned to an owner', 'Status tracked', 'Escalates if it stalls'],
  },
  {
    name: 'Completed',
    summary: 'The request is closed, and the data feeds reporting.',
    detail:
      'Closing the request produces the record the reports and dashboards are built from, so nothing is compiled twice.',
    actions: ['Outcome recorded', 'Requester notified', 'Available to reporting'],
  },
];

/** A row in the service comparison. The three services are peers, not a ranking. */
export interface ServiceComparison {
  readonly name: string;
  readonly for: string;
  readonly fits: string;
  readonly outcome: string;
}

/**
 * The three services side by side.
 *
 * Deliberately not ranked or scored. Each is the right answer for a different
 * situation, and implying an order would be a marketing claim rather than a
 * useful guide. The website's own service copy remains the source of truth for
 * the fuller description.
 */
export const SERVICE_COMPARISON: readonly ServiceComparison[] = [
  {
    name: 'Business Process Automation',
    for: 'Manual or repetitive processes',
    fits:
      'Approvals by email, spreadsheet trackers, paperwork, chasing people for a decision, reports compiled by hand.',
    outcome: 'The work becomes a tracked digital process that routes, notifies and reports on itself.',
  },
  {
    name: 'Custom Business Applications',
    for: 'Unique operational requirements',
    fits:
      'Your process is genuinely different from an off-the-shelf product, or it has to fit precisely into how you already work.',
    outcome: 'Software built around how your operation actually runs, rather than the other way round.',
  },
  {
    name: 'Website Development',
    for: 'A professional digital presence',
    fits:
      'You need a credible, fast website, or a portal where customers and staff can transact with you.',
    outcome: 'A site built to the same standard as the systems behind it, and easy to maintain.',
  },
];
