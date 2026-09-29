import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AdminAuthService } from '../../services/admin-auth.service';

/**
 * Handles the two authentication outcomes across the whole admin API.
 *
 * A `401` means the server side session has ended - it expired, was signed out,
 * or was never valid. The local state is cleared and the administrator is sent
 * to the login screen. A `403` is deliberately left alone: the session is still
 * good, the account simply is not allowed to do that particular thing, and
 * bouncing to the login screen would be misleading.
 *
 * CSRF needs no interceptor. Angular's `HttpClient` already reads the
 * `XSRF-TOKEN` cookie and echoes it in the `X-XSRF-TOKEN` header, which is
 * exactly what Spring Security's cookie CSRF repository expects.
 */
export const adminAuthInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AdminAuthService);
  const router = inject(Router);

  // Credentials are required so the session cookie travels with the request.
  const authorised = request.clone({ withCredentials: true });

  if (!request.url.startsWith(`${environment.apiBaseUrl}/admin`)) {
    return next(authorised);
  }

  return next(authorised).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        auth.clear();
        void router.navigate(['/admin/login'], { queryParams: { expired: 'true' } });
      }
      return throwError(() => error);
    }),
  );
};