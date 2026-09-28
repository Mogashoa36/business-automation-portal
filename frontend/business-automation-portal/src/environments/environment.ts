/**
 * Public runtime configuration.
 *
 * Neither value is a secret: `apiBaseUrl` is the public base URL of the JSON
 * API and `siteUrl` is the public address of this website, used for canonical
 * links and social previews. No credentials, API keys or tokens belong in the
 * frontend - they must never be shipped to the browser.
 */
export const environment = {
  apiBaseUrl: 'http://localhost:8080/api',
  siteUrl: 'http://localhost:4200',
} as const;
