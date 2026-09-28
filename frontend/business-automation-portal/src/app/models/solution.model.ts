import { IconName } from './icon.model';
import { Capability } from './service.model';

/** Content for one of the business solutions built on top of the services. */
export interface SolutionContent {
  readonly slug: string;
  /** Short label used by the navigation. */
  readonly navLabel: string;
  readonly title: string;
  readonly icon: IconName;
  readonly tagline: string;
  readonly summary: string;
  /** Ordered process steps, rendered with the shared process flow component. */
  readonly workflow?: { readonly title: string; readonly steps: readonly string[] };
  readonly capabilities: readonly Capability[];
  readonly benefits: readonly string[];
  readonly ctaLabel: string;
  readonly seo: { readonly title: string; readonly description: string };
}
