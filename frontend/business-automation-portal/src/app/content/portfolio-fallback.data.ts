import { PortfolioProject } from '../models/portfolio-project.model';

/**
 * Offline snapshot of the portfolio case studies.
 *
 * The database is the source of truth for portfolio content. These entries are
 * only rendered when `GET /api/portfolio` cannot be reached, so the portfolio
 * page still has something meaningful to show (and to crawl) when the API is
 * unavailable. They mirror the sample projects seeded by
 * `PortfolioDataSeeder` and are all flagged as sample content - they describe
 * realistic scenarios, they are not presented as work delivered for a client.
 */
export const PORTFOLIO_FALLBACK: readonly PortfolioProject[] = [
  {
    id: null,
    slug: 'approval-automation',
    name: 'Approval Automation Platform',
    industry: 'Professional Services',
    summary:
      'A sample scenario showing how a multi-step, email driven approval process becomes a single digital workflow.',
    problem:
      'Approvals were requested by email, chased manually and tracked in a shared spreadsheet. Nothing showed who was holding up a request, and month end reporting was rebuilt by hand every week.',
    solution:
      'A configurable workflow engine where requests are raised, routed to the correct approvers, escalated automatically when a step stalls, and closed with a full audit trail. Reporting is generated from the same data instead of a separate spreadsheet.',
    technologies: ['Java', 'Spring Boot', 'Spring Data JPA', 'Spring Security', 'PostgreSQL', 'Angular', 'TypeScript'],
    keyFeatures: [
      'Configurable approval steps with conditional routing',
      'Automatic escalation timers',
      'Email and in-app notifications',
      'Complete audit trail of every action',
      'Status dashboards per approver',
      'Exportable management reports',
    ],
    results: [
      'Approvals move in one auditable system instead of email and spreadsheets',
      'Management can see where a request is stuck at any moment',
      'Reporting effort drops from hours per week to a click',
    ],
    imageUrl: null,
    featured: true,
    sampleProject: true,
  },
  {
    id: null,
    slug: 'procurement-workflow',
    name: 'Procurement Workflow System',
    industry: 'Manufacturing',
    summary:
      'A sample scenario covering purchase requests, approvals, supplier records and delivery tracking in one flow.',
    problem:
      'Purchase requests were raised on paper or in inboxes, approved inconsistently and then chased by phone. There was no single view of what was ordered, from which supplier, or what had been delivered.',
    solution:
      'A digital procurement workflow: request, manager approval, procurement review, purchase order, supplier confirmation and delivery completion. Supplier records, approvals and delivery status live in one database that management can report from.',
    technologies: ['Java', 'Spring Boot', 'Spring Data JPA', 'REST API', 'PostgreSQL', 'Angular', 'AWS'],
    keyFeatures: [
      'Purchase request forms with line items',
      'Multi level approval rules',
      'Supplier management records',
      'Purchase order generation',
      'Delivery and receipt tracking',
      'Spend reporting by category and supplier',
    ],
    results: [
      'A clear, auditable trail from request to delivery',
      'Less time spent chasing approvals',
      'Supplier and spend information available in one place',
    ],
    imageUrl: null,
    featured: true,
    sampleProject: true,
  },
  {
    id: null,
    slug: 'operations-dashboard',
    name: 'Operations Reporting Dashboard',
    industry: 'Logistics',
    summary: 'A sample scenario that turns scattered spreadsheets into operational dashboards and routine reports.',
    problem:
      'Operational data lived in several spreadsheets that were updated at different times. Managers worked from numbers that were already out of date, and every report was produced manually.',
    solution:
      'A secure web application that collects operational data once, validates it, and publishes role based dashboards for management and operations, with scheduled and on demand report exports.',
    technologies: ['Java', 'Spring Boot', 'Spring Security', 'REST API', 'PostgreSQL', 'Angular', 'TypeScript'],
    keyFeatures: [
      'Role based dashboards',
      'Live and scheduled reports',
      'Export to spreadsheet and PDF',
      'Data validation on entry',
      'User authentication with access control',
      'Audit history of changes',
    ],
    results: [
      'Managers work from current information rather than yesterday’s spreadsheet',
      'Report preparation becomes repeatable rather than manual',
      'Data quality improves through validation at source',
    ],
    imageUrl: null,
    featured: true,
    sampleProject: true,
  },
];
