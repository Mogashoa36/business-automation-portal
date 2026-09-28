/**
 * Company level details used across the header, footer and contact page.
 *
 * IMPORTANT: the contact values below are placeholders. Replace them with the
 * company's real details before going live - they are intentionally grouped in
 * this single file so there is only one place to update.
 */
export const COMPANY = {
  name: 'Business Automation Company',
  shortName: 'Business Automation',
  legalName: 'Business Automation Company',
  tagline: 'Business Process Automation, Custom Business Applications and Website Development.',
  positioning: 'We help businesses replace manual processes with secure, custom software and modern digital experiences.',
  email: 'info@example.com',
  phone: '+27 00 000 0000',
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
