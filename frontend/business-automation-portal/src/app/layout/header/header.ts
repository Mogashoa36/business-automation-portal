import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  PLATFORM_ID,
  inject,
  signal,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs';

import { COMPANY } from '../../content/company';
import { SERVICES } from '../../content/services.data';
import { SOLUTIONS } from '../../content/solutions.data';
import { Icon } from '../../shared/icon/icon';

/**
 * Minimal sticky navigation. It sits quietly over the page, then gains a
 * blurred backdrop and a hairline once the page scrolls. Services and Solutions
 * open on hover and on click so they stay reachable by keyboard.
 *
 * The scroll signal only ever writes a boolean, so OnPush re-renders when the
 * page crosses the threshold rather than on every scroll event.
 */
@Component({
  selector: 'app-header',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header implements OnDestroy {
  protected readonly company = COMPANY;
  protected readonly services = SERVICES;
  protected readonly solutions = SOLUTIONS;

  protected readonly menuOpen = signal(false);
  protected readonly openDropdown = signal<'services' | 'solutions' | null>(null);
  protected readonly scrolled = signal(false);

  private readonly platformId = inject(PLATFORM_ID);
  private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);

  constructor(router: Router) {
    router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(() => this.closeMenu());
  }

  @HostListener('window:scroll')
  protected onScroll(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.scrolled.set(window.scrollY > 24);
  }

  /** Escape closes the open menu or dropdown and returns focus to the toggle. */
  @HostListener('document:keydown.escape')
  protected onEscape(): void {
    if (this.menuOpen()) {
      this.closeMenu();
      this.host.nativeElement.querySelector<HTMLButtonElement>('.header__toggle')?.focus();
    } else if (this.openDropdown()) {
      this.openDropdown.set(null);
    }
  }

  protected toggleMenu(): void {
    if (this.menuOpen()) {
      this.closeMenu();
      return;
    }

    this.menuOpen.set(true);
    this.openDropdown.set(null);
    this.lockScroll(true);
  }

  protected toggleDropdown(name: 'services' | 'solutions'): void {
    this.openDropdown.update((current) => (current === name ? null : name));
  }

  protected isOpen(name: 'services' | 'solutions'): boolean {
    return this.openDropdown() === name;
  }

  private closeMenu(): void {
    if (!this.menuOpen()) {
      return;
    }

    this.menuOpen.set(false);
    this.lockScroll(false);
  }

  /**
   * Stops the page behind the mobile menu from scrolling. Applied as a class
   * rather than inline styles so it stays inspectable, and cleared on every
   * close path including destruction.
   */
  private lockScroll(locked: boolean): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    document.documentElement.classList.toggle('is-menu-open', locked);
  }

  ngOnDestroy(): void {
    this.lockScroll(false);
  }
}
