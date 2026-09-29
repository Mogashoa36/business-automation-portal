import { ChangeDetectionStrategy, Component, HostListener, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { isAdmin } from '../../models/admin-user.model';
import { AdminAuthService } from '../../services/admin-auth.service';
import { Icon } from '../../shared/icon/icon';

/** One navigation group in the sidebar. */
interface AdminNavGroup {
  readonly label: string;
  readonly items: readonly { label: string; path: string; adminOnly?: boolean }[];
}

/**
 * The administration shell: sidebar, header and the routed content region.
 *
 * The sidebar is a drawer below the tablet breakpoint. It is a single panel
 * rather than a modal, and the page underneath is never scroll-locked, so a
 * mis-tap cannot trap someone on a small screen.
 */
@Component({
  selector: 'app-admin-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Icon],
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.scss',
})
export class AdminShell {
  private readonly auth = inject(AdminAuthService);
  private readonly router = inject(Router);

  protected readonly user = this.auth.user;
  protected readonly canManageAccounts = signal(isAdmin(this.auth.user()));
  protected readonly drawerOpen = signal(false);
  protected readonly signingOut = signal(false);

  /**
   * Only sections with real functionality are listed. Services, solutions and
   * SEO remain static marketing content, so there is nothing to manage there
   * and an empty screen would be worse than no link.
   */
  protected readonly groups: readonly AdminNavGroup[] = [
    {
      label: 'Overview',
      items: [{ label: 'Dashboard', path: '/admin/dashboard' }],
    },
    {
      label: 'Content',
      items: [
        { label: 'Portfolio', path: '/admin/portfolio' },
        { label: 'FAQs', path: '/admin/faqs' },
      ],
    },
    {
      label: 'Leads',
      items: [{ label: 'Contact enquiries', path: '/admin/enquiries' }],
    },
    {
      label: 'System',
      items: [
        { label: 'Images', path: '/admin/images', adminOnly: true },
        { label: 'Admin accounts', path: '/admin/users', adminOnly: true },
        { label: 'Activity log', path: '/admin/activity' },
      ],
    },
    {
      label: 'Account',
      items: [{ label: 'Change password', path: '/admin/change-password' }],
    },
  ];

  constructor() {
    this.canManageAccounts.set(isAdmin(this.auth.user()));
  }

  protected toggleDrawer(): void {
    this.drawerOpen.update((open) => !open);
  }

  protected closeDrawer(): void {
    this.drawerOpen.set(false);
  }

  /** Escape closes the drawer and returns focus to its toggle. */
  @HostListener('document:keydown.escape')
  protected onEscape(): void {
    this.drawerOpen.set(false);
  }

  protected signOut(): void {
    this.signingOut.set(true);
    this.auth.signOut().subscribe({
      // The service clears local state on both outcomes, so the user can never
      // be left looking at a signed-in screen after choosing to sign out.
      next: () => void this.router.navigate(['/admin/login']),
      error: () => void this.router.navigate(['/admin/login']),
    });
  }

  protected initials(): string {
    const user = this.user();
    if (!user) {
      return '';
    }
    const source = user.displayName || user.username;
    return source
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part.charAt(0).toUpperCase())
      .join('');
  }
}