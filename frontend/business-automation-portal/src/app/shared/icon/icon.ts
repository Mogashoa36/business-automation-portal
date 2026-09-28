import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { IconName } from '../../models/icon.model';

/**
 * Small, dependency free icon set.
 *
 * Icons are decorative by default and hidden from assistive technology; a
 * visible label always accompanies them. Every glyph is drawn on a 24x24 grid
 * with a 1.6 stroke so the set stays visually consistent.
 */
@Component({
  selector: 'app-icon',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      [attr.width]="size()"
      [attr.height]="size()"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="1.6"
      stroke-linecap="round"
      stroke-linejoin="round"
      aria-hidden="true"
      focusable="false"
    >
      @switch (name()) {
        @case ('automation') {
          <path d="M4.5 12a7.5 7.5 0 0 1 12.8-5.3L20 9.5" />
          <path d="M20 4.5v5h-5" />
          <path d="M19.5 12a7.5 7.5 0 0 1-12.8 5.3L4 14.5" />
          <path d="M4 19.5v-5h5" />
        }
        @case ('application') {
          <rect x="3" y="4" width="18" height="16" rx="2" />
          <path d="M3 9h18" />
          <path d="M9 9v11" />
          <path d="M12.5 13h5" />
          <path d="M12.5 16.5h3" />
        }
        @case ('website') {
          <circle cx="12" cy="12" r="9" />
          <path d="M3 12h18" />
          <path d="M12 3c2.5 2.4 3.8 5.4 3.8 9s-1.3 6.6-3.8 9c-2.5-2.4-3.8-5.4-3.8-9S9.5 5.4 12 3Z" />
        }
        @case ('workflow') {
          <circle cx="6" cy="6" r="2.5" />
          <circle cx="18" cy="12" r="2.5" />
          <circle cx="6" cy="18" r="2.5" />
          <path d="M8.5 6H14a3.5 3.5 0 0 1 3.5 3.5v.2" />
          <path d="M8.5 18H14a3.5 3.5 0 0 0 3.5-3.5v-.2" />
        }
        @case ('operations') {
          <path d="M4 18a8 8 0 1 1 16 0" />
          <path d="m12 18 4.2-5.2" />
          <path d="M4 18h16" />
        }
        @case ('employee') {
          <circle cx="12" cy="8" r="3.5" />
          <path d="M5 20a7 7 0 0 1 14 0" />
        }
        @case ('procurement') {
          <path d="M3.5 8.5 12 4l8.5 4.5v7L12 20l-8.5-4.5Z" />
          <path d="M12 12.2 3.5 8.5M12 12.2l8.5-3.7M12 12.2V20" />
        }
        @case ('reporting') {
          <path d="M4 20V4" />
          <path d="M4 20h16" />
          <path d="M8 20v-6" />
          <path d="M12.5 20V9" />
          <path d="M17 20v-9.5" />
        }
        @case ('check') {
          <path d="m4.5 12.5 5 5 10-11" />
        }
        @case ('arrow-right') {
          <path d="M4 12h15" />
          <path d="m13 6 6 6-6 6" />
        }
        @case ('shield') {
          <path d="M12 3 19.5 6v5.5c0 4.4-3 8.2-7.5 9.5-4.5-1.3-7.5-5.1-7.5-9.5V6Z" />
          <path d="m9 12 2 2 4-4" />
        }
        @case ('cloud') {
          <path d="M7.5 18.5a4 4 0 0 1-.3-8 5.5 5.5 0 0 1 10.6.4 3.6 3.6 0 0 1-.8 7.6Z" />
        }
        @case ('database') {
          <ellipse cx="12" cy="6" rx="7" ry="3" />
          <path d="M5 6v12c0 1.7 3.1 3 7 3s7-1.3 7-3V6" />
          <path d="M5 12c0 1.7 3.1 3 7 3s7-1.3 7-3" />
        }
        @case ('code') {
          <path d="m9 7-5 5 5 5" />
          <path d="m15 7 5 5-5 5" />
        }
        @case ('mobile') {
          <rect x="7" y="3" width="10" height="18" rx="2" />
          <path d="M11 18.2h2" />
        }
        @case ('menu') {
          <path d="M4 7h16M4 12h16M4 17h16" />
        }
        @case ('close') {
          <path d="m6 6 12 12M18 6 6 18" />
        }
        @case ('mail') {
          <rect x="3" y="5" width="18" height="14" rx="2" />
          <path d="m3.5 6.5 8.5 6.5 8.5-6.5" />
        }
        @case ('phone') {
          <path d="M5 4h4l2 5-2.5 1.5a12 12 0 0 0 5 5L15 13l5 2v4a1 1 0 0 1-1.1 1A16 16 0 0 1 4 5.1 1 1 0 0 1 5 4Z" />
        }
        @case ('pin') {
          <path d="M12 21s7-5.5 7-11a7 7 0 1 0-14 0c0 5.5 7 11 7 11Z" />
          <circle cx="12" cy="10" r="2.5" />
        }
        @case ('clock') {
          <circle cx="12" cy="12" r="8.5" />
          <path d="M12 7.2V12l3 2" />
        }
        @case ('spark') {
          <path d="M13 3 5.8 13.4H11l-1 7.6 7.2-10.4H12Z" />
        }
        @case ('lock') {
          <rect x="5" y="10.5" width="14" height="9.5" rx="2" />
          <path d="M8.5 10.5V8a3.5 3.5 0 0 1 7 0v2.5" />
        }
        @case ('layers') {
          <path d="M12 3.5 21 8l-9 4.5L3 8Z" />
          <path d="m3 12.5 9 4.5 9-4.5" />
          <path d="m3 16.5 9 4.5 9-4.5" />
        }
        @case ('trend-up') {
          <path d="m4 16.5 6-6 4 4 6-6.5" />
          <path d="M15.5 7.5H20V12" />
        }
        @case ('target') {
          <circle cx="12" cy="12" r="8.5" />
          <circle cx="12" cy="12" r="4.5" />
          <circle cx="12" cy="12" r="1" />
        }
        @case ('document') {
          <path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8Z" />
          <path d="M14 3v5h5" />
          <path d="M9 13h6M9 16.5h4" />
        }
      }
    </svg>
  `,
  styles: `
    :host {
      display: inline-flex;
      flex: none;
      line-height: 0;
    }
  `,
})
export class Icon {
  readonly name = input.required<IconName>();
  readonly size = input(24);
}
