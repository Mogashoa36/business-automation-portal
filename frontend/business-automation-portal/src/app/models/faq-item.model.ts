/** A FAQ entry. `published` is only meaningful in the admin portal. */
export interface FaqItem {
  readonly id: number;
  readonly question: string;
  readonly answer: string;
  readonly category: string | null;
  readonly published: boolean;
  readonly displayOrder: number;
}

/** Payload for creating or updating a FAQ in the admin portal. */
export interface FaqPayload {
  readonly question: string;
  readonly answer: string;
  readonly category: string;
  readonly published: boolean;
  readonly displayOrder: number | null;
}

/** A contact enquiry as seen by an administrator. */
export interface Enquiry {
  readonly id: number;
  readonly reference: string;
  readonly fullName: string;
  readonly company: string | null;
  readonly email: string;
  readonly phone: string | null;
  readonly service: string;
  readonly message: string;
  readonly status: EnquiryStatus;
  readonly internalNotes: string | null;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export type EnquiryStatus =
  | 'New'
  | 'In progress'
  | 'Contacted'
  | 'Completed'
  | 'Archived';

/** All statuses, in the order they should appear in a filter. */
export const ENQUIRY_STATUSES: readonly EnquiryStatus[] = [
  'New',
  'In progress',
  'Contacted',
  'Completed',
  'Archived',
];