import { DOCUMENT, Injectable, inject } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { Router } from '@angular/router';

import { environment } from '../../environments/environment';
import { COMPANY } from '../content/company';

export interface SeoConfig {
  /** Full document title, including the company name where it helps. */
  readonly title: string;
  readonly description: string;
  /** Path of the current page, used for the canonical link. */
  readonly path?: string;
  readonly type?: 'website' | 'article';
}

/**
 * Central place for per page SEO metadata.
 *
 * Keeping this in one service means every page sets a title, a description, an
 * Open Graph preview and a canonical URL the same way, and the values are
 * applied during server side rendering so crawlers see them.
 */
@Injectable({ providedIn: 'root' })
export class SeoService {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);
  private readonly router = inject(Router);
  private readonly document = inject(DOCUMENT);

  update(config: SeoConfig): void {
    const url = this.absoluteUrl(config.path ?? this.router.url);
    const type = config.type ?? 'website';

    this.title.setTitle(config.title);
    this.meta.updateTag({ name: 'description', content: config.description });
    this.meta.updateTag({ name: 'robots', content: 'index, follow' });

    this.meta.updateTag({ property: 'og:type', content: type });
    this.meta.updateTag({ property: 'og:site_name', content: COMPANY.name });
    this.meta.updateTag({ property: 'og:title', content: config.title });
    this.meta.updateTag({ property: 'og:description', content: config.description });
    this.meta.updateTag({ property: 'og:url', content: url });
    this.meta.updateTag({ property: 'og:locale', content: 'en_ZA' });

    this.meta.updateTag({ name: 'twitter:card', content: 'summary' });
    this.meta.updateTag({ name: 'twitter:title', content: config.title });
    this.meta.updateTag({ name: 'twitter:description', content: config.description });

    this.setCanonical(url);
  }

  private setCanonical(url: string): void {
    const existing = this.document.head.querySelector<HTMLLinkElement>('link[rel="canonical"]');
    const link = existing ?? this.document.createElement('link');
    link.setAttribute('rel', 'canonical');
    link.setAttribute('href', url);
    if (!existing) {
      this.document.head.appendChild(link);
    }
  }

  private absoluteUrl(path: string): string {
    return new URL(path, environment.siteUrl).toString();
  }
}
