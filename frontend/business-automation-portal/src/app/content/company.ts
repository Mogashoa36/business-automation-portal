/**
 * Company level details used across the header, footer and contact page.
 *
 * Brand: BIZFLOW Technologies. Display name "BIZFLOW", descriptor
 * "Technologies", legal name "Bizflow Technologies".
 *
 * IMPORTANT: `email` and `phone` are intentionally empty until the real
 * details are supplied. The header, footer and contact page hide those rows
 * rather than printing an empty mailto:/tel: link, and the JSON-LD in
 * `index.html` omits them. Nothing on this site is invented.
 */
export const COMPANY = {
  name: 'Bizflow Technologies',
  shortName: 'Bizflow',
  legalName: 'Bizflow Technologies',
  tagline: 'Business Process Automation, Custom Business Applications and Website Development.',
  positioning:
    'We build software that automates business processes, simplifies operations, and turns manual work into efficient digital workflows.',
  email: '',
  phone: '',
  location: 'Johannesburg, South Africa',
  region: 'South Africa',
  businessHours: 'Monday to Friday, 08:00 - 17:00 (SAST)',
  linkedInUrl: '',
} as const;

/** Primary call to action label reused across the site. */
export const PRIMARY_CTA = {
  label: "Let's Automate Your Business",
  link: '/contact',
} as const;
