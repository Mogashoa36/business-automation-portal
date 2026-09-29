import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { PageResponse } from '../models/api-error.model';
import { AdminUser } from '../models/admin-user.model';
import { Enquiry, EnquiryStatus, FaqItem, FaqPayload } from '../models/faq-item.model';
import { PortfolioProject } from '../models/portfolio-project.model';
import { ContentImage, ImageMetadataPayload } from '../models/content-image.model';

/** Real counts shown on the admin dashboard. Never invented. */
export interface DashboardCounts {
  readonly portfolioProjects: number;
  readonly publishedProjects: number;
  readonly draftProjects: number;
  readonly services: number;
  readonly solutions: number;
  readonly faqs: number;
  readonly publishedFaqs: number;
  readonly enquiries: number;
  readonly newEnquiries: number;
  readonly activeEnquiries: number;
  readonly administrators: number;
  readonly publishedContent: number;
}

/** One audit trail row. */
export interface ActivityEntry {
  readonly id: number;
  readonly occurredAt: string;
  readonly actor: string;
  readonly action: string;
  readonly resource: string | null;
  readonly result: string;
  readonly detail: string | null;
}

export interface DashboardSummary {
  readonly counts: DashboardCounts;
  readonly recentActivity: readonly ActivityEntry[];
}

/** The editable fields of a case study, as the admin form collects them. */
export interface PortfolioPayload {
  readonly slug: string;
  readonly name: string;
  readonly industry: string;
  readonly summary: string;
  readonly problem: string;
  readonly solution: string;
  readonly technologies: readonly string[];
  readonly keyFeatures: readonly string[];
  readonly results: readonly string[];
  readonly imageUrl: string;
  readonly category: string;
  readonly featured: boolean;
  readonly published: boolean;
  readonly sampleProject: boolean;
  readonly displayOrder: number | null;
}

/**
 * Admin API client.
 *
 * Enquiry listings are paginated server side, so a large table is never loaded
 * in full. Responses are returned as-is; components own presentation.
 */
@Injectable({ providedIn: 'root' })
export class AdminApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/admin`;

  /* ------------------------------------------------------------- dashboard */

  dashboard(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${this.base}/dashboard`);
  }

  activity(): Observable<readonly ActivityEntry[]> {
    return this.http.get<readonly ActivityEntry[]>(`${this.base}/activity`);
  }

  /* -------------------------------------------------------------- portfolio */

  portfolio(): Observable<readonly PortfolioProject[]> {
    return this.http.get<readonly PortfolioProject[]>(`${this.base}/portfolio`);
  }

  createProject(payload: PortfolioPayload): Observable<PortfolioProject> {
    return this.http.post<PortfolioProject>(`${this.base}/portfolio`, payload);
  }

  updateProject(id: number, payload: PortfolioPayload): Observable<PortfolioProject> {
    return this.http.put<PortfolioProject>(`${this.base}/portfolio/${id}`, payload);
  }

  setPublished(id: number, published: boolean): Observable<PortfolioProject> {
    return this.http.put<PortfolioProject>(`${this.base}/portfolio/${id}/publish`, { published });
  }

  reorderProjects(ids: readonly number[]): Observable<void> {
    return this.http.put<void>(`${this.base}/portfolio/order`, { ids });
  }

  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/portfolio/${id}`);
  }

  /* ------------------------------------------------------------------- faqs */

  faqs(): Observable<readonly FaqItem[]> {
    return this.http.get<readonly FaqItem[]>(`${this.base}/faqs`);
  }

  createFaq(payload: FaqPayload): Observable<FaqItem> {
    return this.http.post<FaqItem>(`${this.base}/faqs`, payload);
  }

  updateFaq(id: number, payload: FaqPayload): Observable<FaqItem> {
    return this.http.put<FaqItem>(`${this.base}/faqs/${id}`, payload);
  }

  deleteFaq(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/faqs/${id}`);
  }

  /* -------------------------------------------------------------- enquiries */

  /** Server side search, status filter and pagination. */
  enquiries(
    status: string,
    search: string,
    page: number,
    size = 20,
  ): Observable<PageResponse<Enquiry>> {
    const params: Record<string, string | number> = { page, size };
    if (status) {
      params['status'] = status;
    }
    if (search) {
      params['search'] = search;
    }
    return this.http.get<PageResponse<Enquiry>>(`${this.base}/enquiries`, { params });
  }

  updateEnquiry(id: number, status: EnquiryStatus | '', internalNotes: string): Observable<Enquiry> {
    return this.http.put<Enquiry>(`${this.base}/enquiries/${id}`, { status, internalNotes });
  }

  /* ----------------------------------------------------------- admin users */

  adminUsers(): Observable<readonly AdminUser[]> {
    return this.http.get<readonly AdminUser[]>(`${this.base}/users`);
  }

  createAdminUser(payload: {
    username: string;
    email: string;
    displayName: string;
    role: 'Administrator' | 'Editor';
    temporaryPassword: string;
  }): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${this.base}/users`, payload);
  }

  updateAdminUser(
    id: number,
    payload: { email: string; displayName: string; role: 'Administrator' | 'Editor'; enabled: boolean },
  ): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.base}/users/${id}`, payload);
  }

  resetAdminPassword(id: number, newPassword: string): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.base}/users/${id}/password`, { newPassword });
  }

  deleteAdminUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/users/${id}`);
  }

  /* ----------------------------------------------------------- site content */

  siteContent(): Observable<readonly { key: string; value: string | null }[]> {
    return this.http.get<readonly { key: string; value: string | null }[]>(`${this.base}/site-content`);
  }

  updateSiteContent(key: string, value: string): Observable<unknown> {
    return this.http.put(`${this.base}/site-content/${key}`, { value });
  }

  /* ----------------------------------------------------------------- images */

  listImages(): Observable<readonly ContentImage[]> {
    return this.http.get<readonly ContentImage[]>(`${this.base}/images`);
  }

  /**
   * Uploads an image as multipart form data.
   *
   * The CSRF token is attached by the framework's interceptor, so it is
   * deliberately not added here: doing it by hand risks a second, stale token
   * being sent alongside the correct one.
   */
  uploadImage(file: File, altText: string, caption: string): Observable<ContentImage> {
    const form = new FormData();
    form.append('file', file, file.name);
    form.append('altText', altText);
    form.append('caption', caption);
    return this.http.post<ContentImage>(`${this.base}/images`, form);
  }

  updateImageMetadata(id: number, payload: ImageMetadataPayload): Observable<ContentImage> {
    return this.http.put<ContentImage>(`${this.base}/images/${id}`, payload);
  }

  deleteImage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/images/${id}`);
  }
}
