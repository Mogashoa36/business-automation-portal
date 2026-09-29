import { HttpErrorResponse } from '@angular/common/http';

import { ApiErrorResponse } from '../models/api-error.model';

/**
 * Builds a safe, human readable message from a failed admin API call.
 *
 * Mirrors the public contact page's helper so both interfaces read the same way.
 * The server already returns neutral text, so this never invents detail and
 * never surfaces a stack trace.
 */
export function describeApiError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as Partial<ApiErrorResponse> | null;

    if (error.status === 0) {
      return 'Could not reach the server. Check your connection and try again.';
    }
    if (error.status === 429) {
      return 'Too many attempts. Please wait a few minutes and try again.';
    }
    if (error.status === 403) {
      return 'That request was refused. Your account may not have permission.';
    }
    if (body && typeof body === 'object' && body.message) {
      return body.message;
    }
  }
  return 'Something went wrong. Please try again.';
}