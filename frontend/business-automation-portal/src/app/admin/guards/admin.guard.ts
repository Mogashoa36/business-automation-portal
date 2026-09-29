import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';

import { AdminAuthService } from '../../services/admin-auth.service';
import { isAdmin } from '../../models/admin-user.model';

/**
 * Keeps unauthenticated visitors out of the administration portal.
 *
 * A guard is a usability feature, not a security boundary. The real boundary is
 * the server, which refuses every `/api/admin/**` request that does not carry a
 * valid session, regardless of what the browser does. This guard only avoids
 * showing a signed-out visitor an empty dashboard.
 *
 * The decision always waits for the server's answer, so a page refresh on a
 * deep link behaves the same as a client side navigation.
 */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AdminAuthService);
  const router = inject(Router);

  return auth.restore().pipe(map((session) => (session ? true : router.createUrlTree(['/admin/login']))));
};

/**
 * Keeps a signed-in administrator off the login screen and sends them to the
 * dashboard instead.
 */
export const loginGuard: CanActivateFn = () => {
  const auth = inject(AdminAuthService);
  const router = inject(Router);

  return auth.restore().pipe(map((session) => (session ? router.createUrlTree(['/admin']) : true)));
};

/**
 * Restricts a route to the ADMIN role, which covers account management, site
 * settings and image uploads.
 *
 * An editor is a legitimate signed-in user, so this does not sign them out. It
 * sends them somewhere they can do useful work instead, which is a better
 * outcome than an error page.
 *
 * Like {@link adminGuard} this is usability only. The server independently
 * refuses `/api/admin/images/**` to an editor, so editing the role check out of
 * the browser gains an attacker nothing.
 */
export const adminRoleGuard: CanActivateFn = () => {
  const auth = inject(AdminAuthService);
  const router = inject(Router);

  return auth.restore().pipe(
    map((session) => {
      if (!session) {
        return router.createUrlTree(['/admin/login']);
      }
      return isAdmin(session.user) ? true : router.createUrlTree(['/admin/dashboard']);
    }),
  );
};