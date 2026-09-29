import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { Enquiry, ENQUIRY_STATUSES, EnquiryStatus } from '../../models/faq-item.model';
import { PageResponse } from '../../models/api-error.model';
import { AdminApiService } from '../../services/admin-api.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';

const PAGE_SIZE = 20;

/**
 * Contact enquiry triage.
 *
 * Search, status filtering and pagination all run on the server, so the table
 * stays responsive and a large inbox is never downloaded in full. Enquiries are
 * private: this data is only ever served from an authenticated endpoint.
 */
@Component({
  selector: 'app-admin-enquiries',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, DatePipe, AdminPageHeader],
  templateUrl: './admin-enquiries.html',
  styleUrl: './admin-enquiries.scss',
})
export class AdminEnquiries {
  private readonly api = inject(AdminApiService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly statuses = ENQUIRY_STATUSES;
  protected readonly enquiries = signal<readonly Enquiry[]>([]);
  protected readonly page = signal<PageResponse<Enquiry> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly statusMessage = signal('');
  protected readonly expanded = signal<number | null>(null);

  private searchTerm = '';

  protected readonly filters = this.formBuilder.nonNullable.group({
    search: [''],
    status: [''],
  });

  constructor() {
    this.load(0);
  }

  protected applyFilters(): void {
    this.searchTerm = this.filters.getRawValue().search?.trim() ?? '';
    this.load(0);
  }

  protected clearFilters(): void {
    this.filters.setValue({ search: '', status: '' });
    this.applyFilters();
  }

  protected changePage(next: number): void {
    const current = this.page();
    if (!current || next < 0 || next >= current.totalPages) {
      return;
    }
    this.load(next);
  }

  protected toggle(id: number): void {
    this.expanded.update((current) => (current === id ? null : id));
  }

  protected setStatus(enquiry: Enquiry, status: EnquiryStatus): void {
    this.api.updateEnquiry(enquiry.id, status, enquiry.internalNotes ?? '').subscribe({
      next: (updated) => {
        this.statusMessage.set(`Updated ${updated.reference}.`);
        this.load(this.page()?.page ?? 0);
      },
      error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
    });
  }

  protected saveNotes(enquiry: Enquiry, notes: string): void {
    this.api.updateEnquiry(enquiry.id, enquiry.status, notes).subscribe({
      next: (updated) => {
        this.statusMessage.set(`Saved notes for ${updated.reference}.`);
        this.load(this.page()?.page ?? 0);
      },
      error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
    });
  }

  protected tone(status: EnquiryStatus): string {
    return {
      New: 'status--new',
      'In progress': 'status--active',
      Contacted: 'status--active',
      Completed: 'status--done',
      Archived: 'status--archived',
    }[status];
  }

  private load(page: number): void {
    this.loading.set(true);
    this.api.enquiries(this.filters.getRawValue().status ?? '', this.searchTerm, page, PAGE_SIZE).subscribe({
      next: (result) => {
        this.page.set(result);
        this.enquiries.set(result.items);
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