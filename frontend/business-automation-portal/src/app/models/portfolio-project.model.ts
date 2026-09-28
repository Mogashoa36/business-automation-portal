/**
 * A portfolio case study. The shape mirrors the `PortfolioProjectResponse`
 * returned by `GET /api/portfolio`, so the same interface describes live
 * database content and the bundled fallback.
 */
export interface PortfolioProject {
  readonly id: number | null;
  readonly slug: string;
  readonly name: string;
  readonly industry: string;
  readonly summary: string;
  readonly problem: string;
  readonly solution: string;
  readonly technologies: readonly string[];
  readonly keyFeatures: readonly string[];
  readonly results: readonly string[];
  readonly imageUrl: string | null;
  readonly featured: boolean;
  /**
   * True for illustrative placeholder content. The website labels these
   * clearly so they are never mistaken for work delivered for a real client.
   */
  readonly sampleProject: boolean;
}
