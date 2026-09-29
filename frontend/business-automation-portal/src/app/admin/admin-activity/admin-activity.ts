import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';

import { ActivityEntry, AdminApiService } from '../../services/admin-api.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';

/**
 * Read-only audit trail of administrative actions.
 *
 * The server decides what is recorded; this view never derives or invents
 * entries. No password, token or secret is stored in the log, so there is
 * nothing sensitive to redact here.
 */
@Component({
  selector: 'app-admin-activity',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe, AdminPageHeader],
  templateUrl: './admin-activity.html',
  styleUrl: './admin-activity.scss',
})
export class AdminActivity {
  private readonly api = inject(AdminApiService);

  protected readonly entries = signal<readonly ActivityEntry[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal('');

  constructor() {
    this.load();
  }

  protected humanise(action: string): string {
    const words = action.toLowerCase().replace(/_/g, ' ').trim();
    return words.charAt(0).toUpperCase() + words.slice(1);
  }

  private load(): void {
    this.loading.set(true);
    this.api.activity().subscribe({
      next: (entries) => {
        this.entries.set(entries);
        this.error.set('');
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.error.set(describeApiError(error));
        this.loading.set(false);
      },
    });
  }
}