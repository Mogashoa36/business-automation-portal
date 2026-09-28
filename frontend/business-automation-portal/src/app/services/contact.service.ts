import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { environment } from '../../environments/environment';
import {
  ApiErrorResponse,
  ContactMessageRequest,
  ContactMessageResponse,
} from '../models/contact-message.model';

/** Submits a contact enquiry to `POST /api/contact`. */
@Injectable({ providedIn: 'root' })
export class ContactService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiBaseUrl}/contact`;

  submit(request: ContactMessageRequest): Observable<ContactMessageResponse> {
    // Values are trimmed here so ordinary typing mistakes (a pasted value with
    // a trailing space) are not rejected by server side validation.
    return this.http
      .post<ContactMessageResponse>(this.endpoint, {
        fullName: request.fullName.trim(),
        company: request.company.trim(),
        email: request.email.trim(),
        phone: request.phone.trim(),
        service: request.service,
        message: request.message.trim(),
      })
      .pipe(
        map((response) => ({
          reference: response.reference,
          service: response.service,
          receivedAt: response.receivedAt,
          confirmation: response.confirmation,
        })),
      );
  }
}

/** Builds a user facing message from a failed API call. */
export function describeApiError(error: unknown): { message: string; fieldErrors: Record<string, string> } {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as Partial<ApiErrorResponse> | string | null;

    if (error.status === 0) {
      return {
        message: 'We could not reach the server. Please check your connection and try again.',
        fieldErrors: {},
      };
    }
    if (error.status === 429) {
      return {
        message: 'Too many enquiries were sent from this connection. Please wait a few minutes and try again.',
        fieldErrors: {},
      };
    }
    if (body && typeof body === 'object') {
      return {
        message: body.message || 'Something went wrong while sending your enquiry. Please try again.',
        fieldErrors: { ...(body.fieldErrors ?? {}) },
      };
    }
    return { message: 'Something went wrong while sending your enquiry. Please try again.', fieldErrors: {} };
  }

  return { message: 'Something went wrong while sending your enquiry. Please try again.', fieldErrors: {} };
}
