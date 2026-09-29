import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { PortfolioProject } from '../../models/portfolio-project.model';
import { AdminApiService, PortfolioPayload } from '../../services/admin-api.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';

/** Splits a textarea into a trimmed, non-empty list. */
function toList(value: string): string[] {
  return value
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => line.length > 0);
}

/**
 * Portfolio case study management.
 *
 * Only administrator supplied content is ever shown. New projects default to
 * `sampleProject: true` so a placeholder can never be mistaken for work
 * delivered for a client - the website renders an explicit label for those.
 */
@Component({
  selector: 'app-admin-portfolio',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, AdminPageHeader],
  templateUrl: './admin-portfolio.html',
  styleUrl: '../shared/admin-form.scss',
})
export class AdminPortfolio {
  private readonly api = inject(AdminApiService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly projects = signal<readonly PortfolioProject[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly statusMessage = signal('');
  protected readonly editingId = signal<number | 'new' | null>(null);
  protected readonly saving = signal(false);

  /** List fields are edited as one entry per line and split on save. */
  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(160)]],
    slug: ['', [Validators.maxLength(80)]],
    industry: ['', [Validators.required, Validators.maxLength(80)]],
    category: ['', [Validators.maxLength(80)]],
    summary: ['', [Validators.required, Validators.maxLength(400)]],
    problem: ['', [Validators.required, Validators.maxLength(4000)]],
    solution: ['', [Validators.required, Validators.maxLength(4000)]],
    technologies: [''],
    keyFeatures: [''],
    results: [''],
    imageUrl: ['', [Validators.maxLength(500)]],
    featured: [false],
    published: [true],
    sampleProject: [true],
  });

  constructor() {
    this.load();
  }

  protected startNew(): void {
    this.form.reset({
      name: '',
      slug: '',
      industry: '',
      category: '',
      summary: '',
      problem: '',
      solution: '',
      technologies: '',
      keyFeatures: '',
      results: '',
      imageUrl: '',
      featured: false,
      published: true,
      sampleProject: true,
    });
    this.editingId.set('new');
    this.statusMessage.set('');
  }

  protected startEdit(project: PortfolioProject): void {
    this.form.reset({
      name: project.name,
      slug: project.slug,
      industry: project.industry,
      category: project.category ?? '',
      summary: project.summary,
      problem: project.problem,
      solution: project.solution,
      technologies: project.technologies.join('\n'),
      keyFeatures: project.keyFeatures.join('\n'),
      results: project.results.join('\n'),
      imageUrl: project.imageUrl ?? '',
      featured: project.featured,
      published: project.published,
      sampleProject: project.sampleProject,
    });
    this.editingId.set(project.id ?? 'new');
    this.statusMessage.set('');
  }

  protected cancel(): void {
    this.editingId.set(null);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.statusMessage.set('Please complete the required fields.');
      return;
    }

    const value = this.form.getRawValue();
    const payload: PortfolioPayload = {
      slug: value.slug.trim(),
      name: value.name.trim(),
      industry: value.industry.trim(),
      category: value.category.trim(),
      summary: value.summary.trim(),
      problem: value.problem.trim(),
      solution: value.solution.trim(),
      technologies: toList(value.technologies),
      keyFeatures: toList(value.keyFeatures),
      results: toList(value.results),
      imageUrl: value.imageUrl.trim(),
      featured: value.featured,
      published: value.published,
      sampleProject: value.sampleProject,
      displayOrder: this.editingId() === 'new' ? this.projects().length + 1 : null,
    };

    this.saving.set(true);
    const target = this.editingId();
    const request =
      target === 'new' || target === null
        ? this.api.createProject(payload)
        : this.api.updateProject(target, payload);

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.editingId.set(null);
        this.statusMessage.set('Case study saved.');
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.statusMessage.set(describeApiError(error));
      },
    });
  }

  protected togglePublished(project: PortfolioProject): void {
    this.api.setPublished(project.id ?? 0, !project.published).subscribe({
      next: (updated) => {
        this.statusMessage.set(`${updated.name} is now ${updated.published ? 'published' : 'a draft'}.`);
        this.load();
      },
      error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
    });
  }

  protected remove(project: PortfolioProject): void {
    if (!confirm(`Delete "${project.name}"? This cannot be undone.`)) {
      return;
    }
    this.api.deleteProject(project.id ?? 0).subscribe({
      next: () => {
        this.statusMessage.set(`Deleted ${project.name}.`);
        this.load();
      },
      error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
    });
  }

  private load(): void {
    this.loading.set(true);
    this.api.portfolio().subscribe({
      next: (projects) => {
        this.projects.set(projects);
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