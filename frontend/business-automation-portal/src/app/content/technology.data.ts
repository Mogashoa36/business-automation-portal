import { TechnologyGroup } from '../models/technology.model';

/**
 * The technology stack the company works with, described in plain language so
 * the page explains capability rather than listing frameworks.
 */
export const TECHNOLOGY_GROUPS: readonly TechnologyGroup[] = [
  {
    id: 'backend',
    title: 'Backend',
    icon: 'code',
    description:
      'Server side systems that hold your business rules, data and security. Everything the business depends on runs here.',
    items: [
      { name: 'Java', note: 'A stable, widely supported foundation for long lived business systems.' },
      { name: 'Spring Boot', note: 'Produces dependable REST services that are quick to build and maintain.' },
      { name: 'Spring Data JPA', note: 'Keeps database access consistent and transactions safe.' },
      { name: 'Spring Security', note: 'Authentication, roles and access control built into the application.' },
      { name: 'REST APIs', note: 'Clean interfaces that let your systems, and our apps, talk to each other.' },
    ],
  },
  {
    id: 'frontend',
    title: 'Frontend',
    icon: 'application',
    description:
      'The interfaces your team actually uses. Fast on a phone in a warehouse and on a laptop in an office.',
    items: [
      { name: 'Angular', note: 'Component based web applications that stay maintainable as they grow.' },
      { name: 'TypeScript', note: 'Catches mistakes before they reach your users.' },
      { name: 'JavaScript', note: 'The standard language of the modern web.' },
      { name: 'HTML', note: 'Semantic, accessible structure that search engines and screen readers understand.' },
      { name: 'CSS', note: 'Layouts engineered to work from 320px phones to large screens.' },
      { name: 'Responsive UI', note: 'Every screen is designed for phone, tablet, laptop and desktop.' },
    ],
  },
  {
    id: 'databases',
    title: 'Databases',
    icon: 'database',
    description:
      'Where your business information is stored safely, backed up and available when you need it.',
    items: [
      { name: 'PostgreSQL', note: 'Our default choice for reliable, transactional business data.' },
      { name: 'Oracle', note: 'Supported where an existing enterprise estate already depends on it.' },
      { name: 'MongoDB', note: 'Used for document style data where the shape varies by record.' },
      { name: 'MySQL', note: 'A practical option for lighter or web focused workloads.' },
    ],
  },
  {
    id: 'cloud',
    title: 'Cloud & Deployment',
    icon: 'cloud',
    description: 'Hosted infrastructure that stays available, is monitored and can be released against repeatedly.',
    items: [
      { name: 'AWS', note: 'Mature hosting used for reliable, right sized deployments.' },
      { name: 'EC2', note: 'Runs the application servers with the capacity your usage needs.' },
      { name: 'S3', note: 'Secure, inexpensive storage for documents, backups and exports.' },
      { name: 'CI/CD', note: 'Automated build, test and release so updates are predictable and reversible.' },
    ],
  },
  {
    id: 'development',
    title: 'Development Practice',
    icon: 'layers',
    description: 'How the work is planned, tracked and built, so you always know what is happening.',
    items: [
      { name: 'Git', note: 'Every change is versioned, reviewable and reversible.' },
      { name: 'GitHub', note: 'Code review and collaboration in one place.' },
      { name: 'Postman', note: 'Documented, repeatable testing of every API the system exposes.' },
      { name: 'Agile / Scrum', note: 'Short, planned iterations with a working demonstration at the end of each one.' },
    ],
  },
];
