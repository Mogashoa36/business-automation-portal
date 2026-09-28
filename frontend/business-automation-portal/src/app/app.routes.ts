import { Routes } from '@angular/router';

/**
 * Every page is lazy loaded so the initial bundle only contains the shell.
 * Page level titles, descriptions and Open Graph tags are set by each
 * component through `SeoService`.
 */
export const routes: Routes = [
  {
    path: '',
    title: 'Business Automation Company | Business Process Automation South Africa',
    loadComponent: () => import('./components/home/home').then((m) => m.Home),
  },
  {
    path: 'services',
    title: 'Services | Business Automation, Custom Apps & Website Development',
    loadComponent: () => import('./components/services/services').then((m) => m.Services),
  },
  {
    path: 'services/business-process-automation',
    loadComponent: () =>
      import('./components/services/business-process-automation/business-process-automation').then(
        (m) => m.BusinessProcessAutomation,
      ),
  },
  {
    path: 'services/custom-business-applications',
    loadComponent: () =>
      import('./components/services/custom-business-applications/custom-business-applications').then(
        (m) => m.CustomBusinessApplications,
      ),
  },
  {
    path: 'services/website-development',
    loadComponent: () =>
      import('./components/services/website-development/website-development').then((m) => m.WebsiteDevelopment),
  },
  {
    path: 'solutions',
    title: 'Solutions | Workflow, Operations, Employees, Procurement & Reporting',
    loadComponent: () => import('./components/solutions/solutions').then((m) => m.Solutions),
  },
  {
    path: 'solutions/workflow-management',
    loadComponent: () =>
      import('./components/solutions/workflow-management/workflow-management').then((m) => m.WorkflowManagement),
  },
  {
    path: 'solutions/operations-management',
    loadComponent: () =>
      import('./components/solutions/operations-management/operations-management').then((m) => m.OperationsManagement),
  },
  {
    path: 'solutions/employee-management',
    loadComponent: () =>
      import('./components/solutions/employee-management/employee-management').then((m) => m.EmployeeManagement),
  },
  {
    path: 'solutions/procurement',
    loadComponent: () => import('./components/solutions/procurement/procurement').then((m) => m.Procurement),
  },
  {
    path: 'solutions/reporting-dashboards',
    loadComponent: () =>
      import('./components/solutions/reporting-dashboards/reporting-dashboards').then((m) => m.ReportingDashboards),
  },
  {
    path: 'about',
    title: 'About | Business Automation Company',
    loadComponent: () => import('./components/about/about').then((m) => m.About),
  },
  {
    path: 'portfolio',
    title: 'Portfolio | Automation & Custom Software Case Studies',
    loadComponent: () => import('./components/portfolio/portfolio').then((m) => m.Portfolio),
  },
  {
    path: 'portfolio/:slug',
    loadComponent: () =>
      import('./components/portfolio/portfolio-detail/portfolio-detail').then((m) => m.PortfolioDetail),
  },
  {
    path: 'technology',
    title: 'Technology | Java, Spring Boot, Angular & Cloud',
    loadComponent: () => import('./components/technology/technology').then((m) => m.Technology),
  },
  {
    path: 'contact',
    title: 'Contact | Start a Business Automation Project',
    loadComponent: () => import('./components/contact/contact').then((m) => m.Contact),
  },
  {
    path: '**',
    title: 'Page not found | Business Automation Company',
    loadComponent: () => import('./components/not-found/not-found').then((m) => m.NotFound),
  },
];
