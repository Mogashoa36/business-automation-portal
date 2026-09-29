import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AdminAuthService } from '../../services/admin-auth.service';
import { describeApiError } from '../../services/admin-error';
import { AdminPageHeader } from '../shared/admin-page-header';

/**
 * Change the signed-in administrator's own password.
 *
 * The current password is re-verified on the server, so a borrowed session
 * alone cannot lock the real owner out. The new password is validated against
 * the same policy the server enforces, and the session ends on success so the
 * old credential cannot keep it alive.
 */
@Component({
  selector: 'app-admin-change-password',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, AdminPageHeader],
  templateUrl: './admin-change-password.html',
  styleUrl: '../shared/admin-form.scss',
})
export class AdminChangePassword {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AdminAuthService);
  private readonly router = inject(Router);

  protected readonly state = signal<'idle' | 'submitting' | 'error' | 'done'>('idle');
  protected readonly message = signal('');

  /** Mirrors the server side policy so the rules are visible up front. */
  protected readonly rules = [
    'At least 12 characters',
    'One uppercase letter',
    'One lowercase letter',
    'One number',
  ];

  protected readonly form = this.formBuilder.nonNullable.group({
    currentPassword: ['', [Validators.required, Validators.maxLength(100)]],
    newPassword: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(100)]],
    confirmPassword: ['', [Validators.required, Validators.maxLength(100)]],
  });

  protected submit(): void {
    this.message.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.state.set('error');
      this.message.set('Please complete every field and meet the password rules.');
      return;
    }

    const value = this.form.getRawValue();
    if (value.newPassword !== value.confirmPassword) {
      this.state.set('error');
      this.message.set('The new password and its confirmation do not match.');
      return;
    }
    if (value.newPassword === value.currentPassword) {
      this.state.set('error');
      this.message.set('Your new password must be different from the current one.');
      return;
    }

    this.state.set('submitting');
    this.auth
      .changePassword(value.currentPassword, value.newPassword)
      .subscribe({
        next: () => {
          // The server ended the session, so the user signs in again with the
          // new password.
          void this.router.navigate(['/admin/login'], { queryParams: { changed: 'true' } });
        },
        error: (error: unknown) => {
          this.state.set('error');
          this.message.set(describeApiError(error));
        },
      });
  }
}