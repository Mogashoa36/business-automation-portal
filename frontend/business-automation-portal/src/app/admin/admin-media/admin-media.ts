import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit, computed, inject, signal, viewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { ContentImage } from '../../models/content-image.model';
import { AdminApiService } from '../../services/admin-api.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';

/** The only image types the server accepts, mirrored for an early warning. */
const ACCEPTED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
const MAX_BYTES = 5 * 1024 * 1024;

/** Human readable file size, without a locale dependent call. */
function formatBytes(bytes: number): string {
  if (bytes < 1024) {
    return `${bytes} B`;
  }
  if (bytes < 1024 * 1024) {
    return `${(bytes / 1024).toFixed(0)} KB`;
  }
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

/**
 * The admin media library.
 *
 * The form follows the flow the brief sets out: choose a file, see what was
 * chosen, confirm it looks right, add alt text and a caption, then save.
 *
 * Validation is duplicated here and on the server. The server is the real
 * boundary and revalidates regardless, but catching an oversized or wrongly
 * typed file before the upload starts is the difference between a helpful
 * message and a long wait followed by one.
 *
 * The preview is a local object URL, revoked whenever it is replaced or the
 * component is destroyed, so choosing several files in a row cannot leak blobs.
 */
@Component({
  selector: 'app-admin-media',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, AdminPageHeader],
  templateUrl: './admin-media.html',
  // Two files: the shared admin form look, then the media specific rules.
  styleUrls: ['../shared/admin-form.scss', './admin-media.scss'],
})
export class AdminMedia implements OnInit, OnDestroy {
  private readonly api = inject(AdminApiService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly fileInput = viewChild<ElementRef<HTMLInputElement>>('fileInput');

  protected readonly images = signal<readonly ContentImage[]>([]);
  protected readonly loading = signal(true);
  protected readonly uploading = signal(false);
  protected readonly statusMessage = signal('');
  protected readonly error = signal('');

  /** Set while a file is staged but not yet uploaded. */
  protected readonly selected = signal<File | null>(null);
  protected readonly previewUrl = signal('');

  protected readonly form = this.formBuilder.nonNullable.group({
    altText: ['', [Validators.required, Validators.maxLength(300)]],
    caption: ['', [Validators.maxLength(300)]],
  });

  /** True when the staged file passes the checks that can be made client side. */
  protected readonly selectionValid = computed(() => {
    const file = this.selected();
    return file !== null && ACCEPTED_TYPES.includes(file.type) && file.size <= MAX_BYTES;
  });

  /** Explains why a staged file will be rejected, or an empty string if fine. */
  protected readonly selectionProblem = computed(() => {
    const file = this.selected();
    if (!file) {
      return '';
    }
    if (!ACCEPTED_TYPES.includes(file.type)) {
      return 'That file type is not supported. Choose a JPEG, PNG, GIF or WebP image.';
    }
    if (file.size > MAX_BYTES) {
      return 'That file is larger than 5 MB. Please choose a smaller image.';
    }
    return '';
  });

  /** File name and size of the staged file, shown next to the preview. */
  protected readonly selectionSummary = computed(() => {
    const file = this.selected();
    return file ? `${file.name} - ${formatBytes(file.size)}` : '';
  });

  ngOnInit(): void {
    this.refresh();
  }

  ngOnDestroy(): void {
    this.clearPreview();
  }

  protected refresh(): void {
    this.loading.set(true);
    this.api.listImages().subscribe({
      next: (images) => {
        this.images.set(images);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.error.set(describeApiError(error));
        this.loading.set(false);
      },
    });
  }

  protected onFileChosen(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.clearPreview();
    const file = input.files?.[0] ?? null;
    this.selected.set(file);
    if (file) {
      // A local object URL, so the right image can be confirmed before upload.
      this.previewUrl.set(URL.createObjectURL(file));
    }
  }

  protected clearSelection(): void {
    this.clearPreview();
    this.selected.set(null);
    // Resets the native control, so picking the same file fires change again.
    const ref = this.fileInput();
    if (ref) {
      ref.nativeElement.value = '';
    }
  }

  protected upload(): void {
    const file = this.selected();
    if (!file || !this.selectionValid() || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.uploading.set(true);
    this.error.set('');
    this.statusMessage.set('');

    const { altText, caption } = this.form.getRawValue();
    this.api.uploadImage(file, altText, caption).subscribe({
      next: (saved) => {
        this.uploading.set(false);
        this.clearSelection();
        this.form.reset({ altText: '', caption: '' });
        this.statusMessage.set('Image uploaded.');
        this.images.update((current) => [saved, ...current]);
      },
      error: (error: unknown) => {
        this.uploading.set(false);
        this.error.set(describeApiError(error));
      },
    });
  }

  protected updateMetadata(image: ContentImage, altText: string, caption: string): void {
    this.api.updateImageMetadata(image.id, { altText, caption }).subscribe({
      next: (updated) => {
        this.images.update((current) => current.map((item) => (item.id === updated.id ? updated : item)));
        this.statusMessage.set('Image details saved.');
        this.error.set('');
      },
      error: (error: unknown) => this.error.set(describeApiError(error)),
    });
  }

  protected remove(image: ContentImage): void {
    if (!confirm(`Delete "${image.originalName}"? Any page using it will no longer show an image.`)) {
      return;
    }
    this.api.deleteImage(image.id).subscribe({
      next: () => {
        this.images.update((current) => current.filter((item) => item.id !== image.id));
        this.statusMessage.set('Image deleted.');
        this.error.set('');
      },
      error: (error: unknown) => this.error.set(describeApiError(error)),
    });
  }

  /** Copies the image URL, which is the only value a content screen needs. */
  protected async copyUrl(image: ContentImage): Promise<void> {
    try {
      await navigator.clipboard.writeText(image.url);
      this.statusMessage.set('Image URL copied.');
    } catch {
      this.statusMessage.set('Could not copy. Select the URL manually.');
    }
  }

  protected formatBytes(bytes: number): string {
    return formatBytes(bytes);
  }

  protected isAccepted(type: string): boolean {
    return ACCEPTED_TYPES.includes(type);
  }

  private clearPreview(): void {
    const url = this.previewUrl();
    if (url) {
      URL.revokeObjectURL(url);
    }
    this.previewUrl.set('');
  }
}