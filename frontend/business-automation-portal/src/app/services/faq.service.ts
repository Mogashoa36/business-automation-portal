import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';

import { environment } from '../../environments/environment';
import { FaqItem } from '../models/faq-item.model';

/**
 * Reads the published FAQs for the public website.
 *
 * Like the portfolio, a bundled snapshot is the fallback so the section still
 * renders something useful when the API cannot be reached. The fallback is
 * marked as such rather than passed off as live.
 */
@Injectable({ providedIn: 'root' })
export class FaqService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiBaseUrl}/faqs`;

  list(): Observable<readonly FaqItem[]> {
    return this.http.get<readonly FaqItem[]>(this.endpoint).pipe(
      catchError(() => of<readonly FaqItem[]>([])),
    );
  }
}