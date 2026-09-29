import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { FaqItem, FaqPayload } from '../../models/faq-item.model';
import { AdminApiService } from '../../services/admin-api.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';

/**
 * FAQ management.
 *
 * Answers are plain text in a textarea and are rendered with interpolation, so
 * there is no HTML to sanitise and no rich text editor to introduce an
 * injection surface.
 */
@Component({
  selector: 'app-admin-faqs',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, AdminPageHeader],
  templateUrl: './admin-faqs.html',
  styleUrl: '../shared/admin-form.scss',
})
export class AdminFaqs {
  private readonly api = inject(AdminApiService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly faqs = signal<readonly FaqItem[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly statusMessage = signal('');
  protected readonly editing = signal<number | 'new' | null>(null);
  protected readonly saving = signal(false);

  protected readonly form = this.formBuilder.nonNullable.group({
    question: ['', [Validators.required, Validators.maxLength(300)]],
    answer: ['', [Validators.required, Validators.maxLength(4000)]],
    category: ['', [Validators.maxLength(80)]],
    published: [true],
    displayOrder: [0],
  });

  constructor() {
    this.load();
  }

  protected startNew(): void {
    this.form.reset({ question: '', answer: '', category: '', published: true, displayOrder: this.faqs().length + 1 });
    this.editing.set('new');
    this.statusMessage.set('');
  }

  protected startEdit(item: FaqItem): void {
    this.form.reset({
      question: item.question,
      answer: item.answer,
      category: item.category ?? '',
      published: item.published,
      displayOrder: item.displayOrder,
    });
    this.editing.set(item.id);
    this.statusMessage.set('');
  }

  protected cancel(): void {
    this.editing.set(null);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.statusMessage.set('Please correct the highlighted fields.');
      return;
    }

    const value = this.form.getRawValue();
    const payload: FaqPayload = {
      question: value.question.trim(),
      answer: value.answer.trim(),
      category: value.category.trim(),
      published: value.published,
      displayOrder: Number.isFinite(value.displayOrder) ? value.displayOrder : null,
    };

    this.saving.set(true);
    const target = this.editing();
    const request = target === 'new' || target === null
      ? this.api.createFaq(payload)
      : this.api.updateFaq(target, payload);

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.editing.set(null);
        this.statusMessage.set('FAQ saved.');
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.statusMessage.set(describeApiError(error));
      },
    });
  }

  protected remove(item: FaqItem): void {
    // Deletion is permanent, so it is confirmed before it happens.
    if (!confirm(`Delete this FAQ?\n\n${item.question}`)) {
      return;
    }
    this.api.deleteFaq(item.id).subscribe({
      next: () => {
        this.statusMessage.set('FAQ deleted.');
        this.load();
      },
      error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
    });
  }

  private load(): void {
    this.loading.set(true);
    this.api.faqs().subscribe({
      next: (items) => {
        this.faqs.set(items);
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