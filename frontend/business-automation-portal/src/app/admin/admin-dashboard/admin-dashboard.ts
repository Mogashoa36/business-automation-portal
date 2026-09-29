import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';

import { AdminApiService, ActivityEntry, DashboardCounts } from '../../services/admin-api.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';
import { AdminStats, AdminStat } from '../shared/admin-stats';

/**
 * Admin dashboard.
 *
 * Every figure comes from `GET /api/admin/dashboard`, which counts real rows.
 * Nothing here is hard coded, and an empty installation correctly shows zeros.
 */
@Component({
  selector: 'app-admin-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe, AdminPageHeader, AdminStats],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss',
})
export class AdminDashboard {
  private readonly api = inject(AdminApiService);

  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly counts = signal<DashboardCounts | null>(null);
  protected readonly activity = signal<readonly ActivityEntry[]>([]);

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.api.dashboard().subscribe({
      next: (summary) => {
        this.counts.set(summary.counts);
        this.activity.set(summary.recentActivity);
        this.error.set('');
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.error.set(describeApiError(error));
        this.loading.set(false);
      },
    });
  }

  /** Maps the counts onto the tiles, describing exactly what each one counts. */
  protected stats(): readonly AdminStat[] {
    const counts = this.counts();
    if (!counts) {
      return [];
    }

    return [
      { label: 'Portfolio projects', value: counts.portfolioProjects, hint: `${counts.draftProjects} draft` },
      {
        label: 'Services',
        value: counts.services,
        hint: 'Published on the site',
        tone: 'muted',
      },
      { label: 'Solutions', value: counts.solutions, hint: 'Published on the site', tone: 'muted' },
      {
        label: 'Contact enquiries',
        value: counts.enquiries,
        hint: `${counts.newEnquiries} new`,
        tone: counts.newEnquiries > 0 ? 'accent' : 'default',
      },
      {
        label: 'Published content',
        value: counts.publishedContent,
        hint: `${counts.publishedProjects} projects + ${counts.publishedFaqs} FAQs`,
      },
    ];
  }

  /** Turns ADMIN_LOGIN into "Admin login" for the activity list. */
  protected humanise(action: string): string {
    const words = action.toLowerCase().replace(/_/g, ' ').trim();
    return words.charAt(0).toUpperCase() + words.slice(1);
  }
}