import { COMPANY } from '../content/company';

/**
 * Company photography.
 *
 * IMPORTANT: no photograph ships with the website. BIZFLOW is a real business
 * with no supplied photography, and a stock image of someone else's office
 * would misrepresent it. `PHOTO` is therefore an empty slot.
 *
 * When a genuine photograph becomes available, drop its file into
 * `public/images/` and set `src` and `alt` here. The layout, the aspect ratio
 * and the accessibility behaviour are already in place, so the swap changes one
 * line rather than a template.
 *
 * `alt` must describe the image, not repeat the heading. Until a real photo
 * exists the slot renders a designed placeholder that is explicitly marked as
 * such to assistive technology, so a visitor is never told there is a photo
 * there when there is not.
 */
export interface CompanyPhoto {
  /** Path under `public/`, e.g. `/images/studio.jpg`. Empty when not supplied. */
  readonly src: string;
  /** Concise description of what the photograph shows. */
  readonly alt: string;
  /** Optional caption shown under the image. */
  readonly caption: string;
}

export const PHOTO: CompanyPhoto = {
  src: '',
  alt: '',
  caption: 'Bizflow Technologies, Johannesburg',
};

/** True when a real photograph has been supplied. */
export function hasPhoto(photo: CompanyPhoto = PHOTO): boolean {
  return photo.src.trim().length > 0 && photo.alt.trim().length > 0;
}

/** Company facts shown alongside the photograph. Nothing invented. */
export const COMPANY_FACTS: readonly { label: string; value: string }[] = [
  { label: 'Based in', value: COMPANY.location },
  { label: 'Hours', value: COMPANY.businessHours },
  { label: 'Focus', value: 'Business process automation and custom software' },
];