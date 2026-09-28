import { IconName } from './icon.model';

/** A titled capability used across the service and solution pages. */
export interface Capability {
  readonly title: string;
  readonly description: string;
}

/**
 * Content for one of the three services. This is static marketing content, so
 * it lives in the application rather than in the database - the backend only
 * persists the genuinely dynamic data (contact enquiries and portfolio
 * projects).
 */
export interface ServiceContent {
  readonly slug: string;
  /** Short label used by the navigation. */
  readonly navLabel: string;
  readonly title: string;
  readonly icon: IconName;
  /** One line positioning statement. */
  readonly tagline: string;
  readonly summary: string;
  /** The lead service is presented as the company's specialisation. */
  readonly isPrimary: boolean;
  /** Manual approaches the service replaces. */
  readonly replaces: readonly string[];
  /** Ordered process steps, rendered with the shared process flow component. */
  readonly workflow?: { readonly title: string; readonly steps: readonly string[] };
  readonly capabilities: readonly Capability[];
  readonly outcomes: readonly string[];
  readonly ctaLabel: string;
  readonly seo: { readonly title: string; readonly description: string };
}
