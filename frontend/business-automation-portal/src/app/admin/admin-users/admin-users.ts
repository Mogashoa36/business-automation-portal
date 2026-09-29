import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AdminUser } from '../../models/admin-user.model';
import { AdminApiService } from '../../services/admin-api.service';
import { AdminAuthService } from '../../services/admin-auth.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';

/**
 * Administrator account management.
 *
 * The route is reachable by administrators, and the server refuses an editor
 * regardless of what this screen renders. Two safeguards are also enforced
 * server side: the last enabled administrator cannot be removed or demoted, and
 * nobody can disable their own account.
 *
 * A password is never displayed. Resetting one sets a new temporary secret that
 * its owner must change at the next sign in.
 */
@Component({
  selector: 'app-admin-users',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, DatePipe, AdminPageHeader],
  templateUrl: './admin-users.html',
  styleUrl: '../shared/admin-form.scss',
})
export class AdminUsers {
  private readonly api = inject(AdminApiService);
  private readonly auth = inject(AdminAuthService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly users = signal<readonly AdminUser[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal('');
  protected readonly statusMessage = signal('');
  protected readonly creating = signal(false);
  protected readonly saving = signal(false);
  protected readonly currentUserId = signal<number | null>(null);

  protected readonly createForm = this.formBuilder.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(60)]],
    email: ['', [Validators.required, Validators.email]],
    displayName: ['', [Validators.required, Validators.maxLength(120)]],
    role: ['Editor' as 'Administrator' | 'Editor'],
    temporaryPassword: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(100)]],
  });

  constructor() {
    this.currentUserId.set(this.auth.user()?.id ?? null);
    this.load();
  }

  protected isSelf(user: AdminUser): boolean {
    return user.id === this.currentUserId();
  }

  protected startCreate(): void {
    this.createForm.reset({
      username: '',
      email: '',
      displayName: '',
      role: 'Editor',
      temporaryPassword: '',
    });
    this.creating.set(true);
    this.statusMessage.set('');
  }

  protected cancelCreate(): void {
    this.creating.set(false);
  }

  protected create(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      this.statusMessage.set('Please complete every field and meet the password rules.');
      return;
    }

    this.saving.set(true);
    const value = this.createForm.getRawValue();
    this.api
      .createAdminUser({
        username: value.username.trim(),
        email: value.email.trim(),
        displayName: value.displayName.trim(),
        role: value.role,
        temporaryPassword: value.temporaryPassword,
      })
      .subscribe({
        next: (created) => {
          this.saving.set(false);
          this.creating.set(false);
          // The temporary password is never echoed back, so the message says
          // what to do without repeating the secret.
          this.statusMessage.set(
            `Created ${created.username}. Share the temporary password securely; they must change it at first sign in.`,
          );
          this.load();
        },
        error: (error: unknown) => {
          this.saving.set(false);
          this.statusMessage.set(describeApiError(error));
        },
      });
  }

  protected toggleEnabled(user: AdminUser): void {
    this.api
      .updateAdminUser(user.id, {
        email: user.email,
        displayName: user.displayName,
        role: user.role,
        enabled: !user.enabled,
      })
      .subscribe({
        next: (updated) => {
          this.statusMessage.set(`${updated.username} is now ${updated.enabled ? 'enabled' : 'disabled'}.`);
          this.load();
        },
        error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
      });
  }

  protected changeRole(user: AdminUser, role: 'Administrator' | 'Editor'): void {
    this.api
      .updateAdminUser(user.id, {
        email: user.email,
        displayName: user.displayName,
        role,
        enabled: user.enabled,
      })
      .subscribe({
        next: (updated) => {
          this.statusMessage.set(`${updated.username} is now ${updated.role.toLowerCase()}.`);
          this.load();
        },
        error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
      });
  }

  /** Prompts for a new secret rather than showing the existing one. */
  protected resetPassword(user: AdminUser): void {
    const next = prompt(
      `Enter a new temporary password for ${user.username}.\n\n` +
        'It must be at least 12 characters and include an uppercase letter, a lowercase letter and a number. ' +
        'They must change it at their next sign in.',
    );
    if (!next) {
      return;
    }

    this.api.resetAdminPassword(user.id, next).subscribe({
      next: (updated) => {
        this.statusMessage.set(
          `Password reset for ${updated.username}. Share the new password securely; it is never shown again.`,
        );
      },
      error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
    });
  }

  protected remove(user: AdminUser): void {
    if (!confirm(`Remove ${user.username}? They will no longer be able to sign in.`)) {
      return;
    }
    this.api.deleteAdminUser(user.id).subscribe({
      next: () => {
        this.statusMessage.set(`Removed ${user.username}.`);
        this.load();
      },
      error: (error: unknown) => this.statusMessage.set(describeApiError(error)),
    });
  }

  private load(): void {
    this.loading.set(true);
    this.api.adminUsers().subscribe({
      next: (users) => {
        this.users.set(users);
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