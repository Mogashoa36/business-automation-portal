import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AdminAuthService } from '../../services/admin-auth.service';
import { describeApiError } from '../../services/admin-error';

type SignInState = 'idle' | 'submitting' | 'error';

/**
 * Administrator sign in.
 *
 * The form is a convenience only. Verification happens entirely on the server,
 * where the submitted password is compared against a BCrypt hash; nothing here
 * decides whether a sign in is allowed, and the password is never stored.
 */
@Component({
  selector: 'app-admin-login',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-login.html',
  styleUrl: './admin-login.scss',
})
export class AdminLogin {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AdminAuthService);
  private readonly router = inject(Router);

  protected readonly state = signal<SignInState>('idle');
  protected readonly message = signal('');

  /** True when the session ended rather than the password being wrong. */
  protected readonly sessionExpired = signal(false);

  protected readonly form = this.formBuilder.nonNullable.group({
    identifier: ['', [Validators.required, Validators.maxLength(180)]],
    password: ['', [Validators.required, Validators.maxLength(100)]],
  });

  constructor() {
    // Reached after an expired session, so say so rather than implying a typo.
    this.sessionExpired.set(
      new URLSearchParams(typeof location === 'undefined' ? '' : location.search).get('expired') === 'true',
    );
  }

  protected submit(): void {
    this.message.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.state.set('error');
      this.message.set('Enter your email or username and your password.');
      return;
    }

    this.state.set('submitting');
    const { identifier, password } = this.form.getRawValue();

    this.auth.signIn(identifier, password).subscribe({
      next: (session) => {
        // The form is cleared so the password does not linger in the DOM.
        this.form.reset();
        void this.router.navigate([session.mustChangePassword ? '/admin/change-password' : '/admin/dashboard']);
      },
      error: (error: unknown) => {
        this.state.set('error');
        this.message.set(describeApiError(error));
      },
    });
  }
}