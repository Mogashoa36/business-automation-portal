/**
 * An authenticated administrator.
 *
 * Mirrors the `AdminUserResponse` returned by the admin API. There is
 * deliberately no password field: the server never returns one, and the client
 * has no place to put it.
 */
export interface AdminUser {
  readonly id: number;
  readonly username: string;
  readonly email: string;
  readonly displayName: string;
  readonly role: AdminRoleName;
  readonly enabled: boolean;
  /** True when the account must set its own password before continuing. */
  readonly mustChangePassword: boolean;
  readonly lastLoginAt: string | null;
}

/** Role names exactly as the API returns them. */
export type AdminRoleName = 'Administrator' | 'Editor';

/** Sign in result. */
export interface AdminSession {
  readonly user: AdminUser;
  readonly mustChangePassword: boolean;
}

/** True when the account may manage accounts and site settings. */
export function isAdmin(user: AdminUser | null): boolean {
  return user?.role === 'Administrator';
}