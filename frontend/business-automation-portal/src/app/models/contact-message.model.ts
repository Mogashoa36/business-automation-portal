/** Options offered by the contact form, matching the values the API accepts. */
export const SERVICE_OPTIONS = [
  'Business Process Automation',
  'Custom Business Application',
  'Website Development',
  'Workflow Management',
  'Operations Management',
  'Employee Management',
  'Procurement',
  'Reporting & Dashboards',
  'Other',
] as const;

export type ServiceOption = (typeof SERVICE_OPTIONS)[number];

/** Payload sent to `POST /api/contact`. */
export interface ContactMessageRequest {
  readonly fullName: string;
  readonly company: string;
  readonly email: string;
  readonly phone: string;
  readonly service: string;
  readonly message: string;
}

/** Receipt returned by `POST /api/contact`. */
export interface ContactMessageResponse {
  readonly reference: string;
  readonly service: string;
  readonly receivedAt: string;
  readonly confirmation: string;
}

/** Error payload returned by the API for every failed request. */
export interface ApiErrorResponse {
  readonly timestamp?: string;
  readonly status: number;
  readonly error: string;
  readonly message: string;
  readonly fieldErrors: Readonly<Record<string, string>>;
  readonly path?: string;
}
