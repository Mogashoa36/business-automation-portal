import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';

import { environment } from '../../environments/environment';
import { AdminSession, AdminUser } from '../models/admin-user.model';

/**
 * Client side view of the administrator session.
 *
 * IMPORTANT: this holds no secret. Authentication is a server side session
 * carried by an `HttpOnly` cookie that JavaScript cannot read, so there is no
 * token to store in `localStorage` and nothing here that could be exfiltrated
 * by an XSS bug. The service only remembers *who* is signed in so the UI can
 * render the right navigation, and the server re-verifies every request.
 */
@Injectable({ providedIn: 'root' })
export class AdminAuthService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/admin/auth`;

  /** Null until `restore()` has confirmed the session with the server. */
  private readonly currentUser = signal<AdminUser | null>(null);
  private readonly checked = signal(false);

  readonly user = this.currentUser.asReadonly();
  readonly isSignedIn = computed(() => this.currentUser() !== null);
  readonly mustChangePassword = computed(() => this.currentUser()?.mustChangePassword ?? false);

  /**
   * Confirms whether an existing server session is still valid. Used by the
   * route guard on a page refresh, when the in-memory state is gone.
   */
  restore(): Observable<AdminSession | null> {
    return this.http.get<AdminSession>(`${this.base}/me`).pipe(
      tap((session) => this.currentUser.set(session.user)),
      // A 401 here is the normal "not signed in" answer, not an error worth
      // raising. The guard decides what to do with the resolved value.
      catchError(() => {
        this.currentUser.set(null);
        return of(null);
      }),
      tap(() => this.checked.set(true)),
    );
  }

  /** Signs in. The password is sent once and never retained by this service. */
  signIn(identifier: string, password: string): Observable<AdminSession> {
    return this.http
      .post<AdminSession>(`${this.base}/login`, { identifier: identifier.trim(), password })
      .pipe(tap((session) => this.currentUser.set(session.user)));
  }

  /**
   * Ends the session. The server invalidates it, and the local state is
   * cleared regardless of the response so the UI never shows a stale user.
   */
  signOut(): Observable<void> {
    return this.http.post<void>(`${this.base}/logout`, {}).pipe(
      tap({
        next: () => this.clear(),
        error: () => this.clear(),
      }),
    );
  }

  /**
   * Replaces the signed-in administrator's password. The server re-verifies
   * the current one and ends the session on success, so this only reports the
   * outcome.
   */
  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.base}/change-password`, { currentPassword, newPassword });
  }

  /** Clears local state after a password change, which ends the session. */
  clear(): void {
    this.currentUser.set(null);
  }
}