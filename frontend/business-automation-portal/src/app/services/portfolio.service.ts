import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';

import { environment } from '../../environments/environment';
import { PORTFOLIO_FALLBACK } from '../content/portfolio-fallback.data';
import { PortfolioProject } from '../models/portfolio-project.model';

export interface PortfolioResult {
  readonly projects: readonly PortfolioProject[];
  /**
   * True when the API could not be reached and the bundled snapshot is being
   * shown instead. The UI says so rather than pretending the data is live.
   */
  readonly fromFallback: boolean;
}

/**
 * Reads portfolio case studies from the backend.
 *
 * Portfolio content lives in the database so real projects can be added without
 * a frontend release. If the API is unavailable the bundled snapshot is used
 * so the page still renders something useful.
 */
@Injectable({ providedIn: 'root' })
export class PortfolioService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiBaseUrl}/portfolio`;

  list(): Observable<PortfolioResult> {
    return this.http.get<PortfolioProject[]>(this.endpoint).pipe(
      map((projects) => ({ projects: projects.map(normalise), fromFallback: false })),
      catchError(() => of({ projects: PORTFOLIO_FALLBACK, fromFallback: true })),
    );
  }

  /** @returns the project, or `null` when no case study uses that slug. */
  bySlug(slug: string): Observable<PortfolioProject | null> {
    return this.http.get<PortfolioProject>(`${this.endpoint}/${encodeURIComponent(slug)}`).pipe(
      map((project) => normalise(project)),
      catchError(() => of(PORTFOLIO_FALLBACK.find((project) => project.slug === slug) ?? null)),
    );
  }
}

/**
 * Defensive mapping so a partial payload can never break the template, and so
 * an older API response still satisfies the interface.
 *
 * `published` defaults to `true` because the public endpoint only ever returns
 * published projects, and `category` is optional.
 */
function normalise(project: PortfolioProject): PortfolioProject {
  return {
    ...project,
    technologies: project.technologies ?? [],
    keyFeatures: project.keyFeatures ?? [],
    results: project.results ?? [],
    published: project.published ?? true,
    category: project.category ?? null,
  };
}
