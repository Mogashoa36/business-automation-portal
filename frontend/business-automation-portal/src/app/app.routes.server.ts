import { RenderMode, ServerRoute } from '@angular/ssr';

/**
 * All routes are rendered on the server at request time rather than
 * prerendered at build time. The portfolio pages read from the API, so
 * rendering them on demand guarantees the markup always matches the current
 * database content and the build never depends on a running backend.
 */
export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Server,
  },
];
