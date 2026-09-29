/** The shared JSON error shape returned by every API endpoint. */
export interface ApiErrorResponse {
  readonly timestamp?: string;
  readonly status: number;
  readonly error: string;
  readonly message: string;
  readonly fieldErrors?: Record<string, string>;
  readonly path?: string;
}

/** One page of admin table rows, with totals computed server side. */
export interface PageResponse<T> {
  readonly items: readonly T[];
  readonly page: number;
  readonly size: number;
  readonly totalItems: number;
  readonly totalPages: number;
  readonly first: boolean;
  readonly last: boolean;
}