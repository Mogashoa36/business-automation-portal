import { ServiceContent } from '../models/service.model';

/**
 * The three services the company delivers.
 *
 * Business Process Automation is the primary specialisation, Custom Business
 * Applications is the core delivery capability and Website Development supports
 * both.
 */
export const SERVICES = [
  {
    slug: 'business-process-automation',
    navLabel: 'Business Process Automation',
    title: 'Business Process Automation',
    icon: 'automation',
    tagline: 'Replace manual, error-prone processes with digital workflows that run themselves.',
    summary:
      'We transform the manual processes that slow your business down - Excel trackers, email approvals, paperwork and repetitive administration - into digital workflows that route, notify, escalate and report automatically.',
    isPrimary: true,
    replaces: [
      'Excel based workflows and trackers',
      'Email approval chains',
      'Paper based forms and sign-off',
      'Manually compiled reports',
      'Manual task allocation',
      'Repetitive administrative work',
    ],
    workflow: {
      title: 'A typical automated request',
      steps: ['Request', 'Approval', 'Task Assignment', 'Processing', 'Completion', 'Reporting'],
    },
    capabilities: [
      {
        title: 'Workflow automation',
        description:
          'Business rules decide what happens next, so requests move forward without anyone chasing them by hand.',
      },
      {
        title: 'Approval workflows',
        description:
          'Multi level approvals with conditional routing, delegation and a clear decision history for every request.',
      },
      {
        title: 'Notifications',
        description: 'Email and in-app alerts tell the right person about the right task at the right moment.',
      },
      {
        title: 'Task management',
        description: 'Work is assigned, prioritised and tracked so nothing is lost between teams or inboxes.',
      },
      {
        title: 'Escalations',
        description: 'Steps that stall are escalated automatically so problems surface early instead of at month end.',
      },
      {
        title: 'Audit trails',
        description: 'Every action, approval and change is recorded so decisions can be explained and evidenced later.',
      },
      {
        title: 'Reporting and dashboards',
        description: 'Live dashboards and routine reports come from the same data the process runs on.',
      },
      {
        title: 'Works with what you already use',
        description: 'Existing systems, spreadsheets and email are connected rather than replaced where it makes sense.',
      },
    ],
    outcomes: [
      'Far less time spent on repetitive administration',
      'Fewer missed steps, lost requests and approval bottlenecks',
      'A single, trustworthy view of where every request stands',
      'Reporting that updates itself instead of being rebuilt each week',
      'Processes that scale as the business grows',
    ],
    ctaLabel: 'Automate Your Process',
    seo: {
      title: 'Business Process Automation | Workflow Automation South Africa',
      description:
        'Replace manual processes, Excel trackers and email approvals with secure digital workflows, automated approvals, escalations and real-time reporting.',
    },
  },
  {
    slug: 'custom-business-applications',
    navLabel: 'Custom Business Applications',
    title: 'Custom Business Applications',
    icon: 'application',
    tagline: 'Software designed around how your business actually works.',
    summary:
      'Off the shelf software rarely matches the way an organisation really operates. We design and build custom applications around your specific business requirements, so the system fits the process instead of forcing the process into the system.',
    isPrimary: false,
    replaces: [
      'Business critical spreadsheets',
      'Shared drives full of templates',
      'Disconnected internal tools',
      'Manual administration systems',
      'Reporting put together in a hurry',
    ],
    workflow: {
      title: 'How we build your application',
      steps: ['Discover', 'Design', 'Build', 'Test', 'Deploy', 'Support'],
    },
    capabilities: [
      {
        title: 'Custom workflows',
        description: 'The application follows your real process, including the exceptions everyone already knows about.',
      },
      {
        title: 'User authentication',
        description: 'Secure sign in for every user, with session handling and account controls.',
      },
      {
        title: 'Role based access',
        description: 'People only see and change what their role allows, from reception staff to executive reporting.',
      },
      {
        title: 'Database driven applications',
        description: 'Structured, reliable data storage with validation, history and reporting built in.',
      },
      {
        title: 'Dashboards and reporting',
        description: 'Operational and management views that reflect the same data your team maintains.',
      },
      {
        title: 'Responsive interfaces',
        description: 'Usable on desktop, tablet and mobile so approvals and tasks travel with your people.',
      },
      {
        title: 'Cloud deployment',
        description: 'Deployed to reliable cloud infrastructure with backups and a documented release process.',
      },
    ],
    outcomes: [
      'One system of record instead of a dozen spreadsheets',
      'Software that matches your process rather than approximating it',
      'Controlled access to sensitive business information',
      'Reports the business trusts because the data is validated at source',
      'A foundation that grows with the business instead of being rebuilt',
    ],
    ctaLabel: 'Build Your Application',
    seo: {
      title: 'Custom Business Applications | Business Software Development',
      description:
        'Custom business applications, portals and internal systems built around your requirements, with role based access, dashboards, reporting and cloud deployment.',
    },
  },
  {
    slug: 'website-development',
    navLabel: 'Website Development',
    title: 'Website Development',
    icon: 'website',
    tagline: 'Professional business websites that present your business properly.',
    summary:
      'We build corporate and business websites that look credible, load quickly and work on every screen. Website development supports our software and automation work - the same team that builds secure internal systems builds the front door to your business.',
    isPrimary: false,
    replaces: [
      'Outdated or unprofessional websites',
      'Sites that are slow or broken on mobile',
      'Websites that are hard to maintain',
      'Brochure sites with no way to capture enquiries',
    ],
    capabilities: [
      {
        title: 'Corporate and business websites',
        description: 'Clear, credible sites that explain what the business does and how to start a conversation.',
      },
      {
        title: 'Landing and service pages',
        description: 'Focused pages for a specific service, campaign or audience.',
      },
      {
        title: 'Portfolio websites',
        description: 'Showcase work in a way that supports the reputation you are building.',
      },
      {
        title: 'Responsive design',
        description: 'Layouts engineered for phones, tablets, laptops and large screens from 320px upwards.',
      },
      {
        title: 'Contact forms and analytics',
        description: 'Validated enquiry forms that reach your inbox, plus analytics to see what actually works.',
      },
      {
        title: 'SEO fundamentals',
        description: 'Clean page titles, meta descriptions, semantic structure and fast loading pages.',
      },
      {
        title: 'Hosting and deployment',
        description: 'Deployment and hosting set up properly, with a documented release process.',
      },
      {
        title: 'Website maintenance',
        description: 'Ongoing updates, monitoring and support so the site keeps working.',
      },
    ],
    outcomes: [
      'A credible first impression for customers and partners',
      'Enquiries captured automatically instead of lost in an inbox',
      'A site your own team can update without developer help',
      'Fast, accessible pages that work on every device',
      'A website that can later connect to your business systems',
    ],
    ctaLabel: 'Build My Website',
    seo: {
      title: 'Website Development | Professional Business Websites',
      description:
        'Professional corporate, business and service website development with responsive design, contact forms, SEO fundamentals, analytics, hosting and maintenance.',
    },
  },
] as const satisfies readonly ServiceContent[];

export function findService(slug: string): ServiceContent | undefined {
  return SERVICES.find((service) => service.slug === slug);
}
