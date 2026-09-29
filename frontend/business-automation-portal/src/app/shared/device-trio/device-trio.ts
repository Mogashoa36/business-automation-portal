import { ChangeDetectionStrategy, Component } from '@angular/core';

import { Reveal } from '../reveal/reveal';

/**
 * Desktop, tablet and phone showing the same page.
 *
 * This is the clearest way to make the point that a BIZFLOW website is
 * responsive: one composition, three viewports, the same content reflowed. It
 * replaces what would otherwise be three separate screenshots.
 *
 * Purely decorative, so the group is `aria-hidden`. The responsive claim is made
 * in the surrounding copy and no information is available only through this
 * image.
 *
 * On narrow screens the tablet and phone tuck behind the desktop rather than
 * shrinking to illegibility, which is how device showcases are normally handled
 * and avoids a row of postage stamps.
 */
@Component({
  selector: 'app-device-trio',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Reveal],
  template: `
    <div class="trio" appMockup aria-hidden="true">
      <div class="device device--desktop">
        <div class="device__chrome">
          <span class="device__dot"></span><span class="device__dot"></span><span class="device__dot"></span>
          <span class="device__url">yoursite.co.za</span>
        </div>
        <div class="device__screen">
          <span class="device__nav"></span>
          <span class="device__hero-line device__hero-line--lg"></span>
          <span class="device__hero-line"></span>
          <div class="device__cols">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>

      <div class="device device--tablet">
        <div class="device__chrome device__chrome--slim">
          <span class="device__url">yoursite.co.za</span>
        </div>
        <div class="device__screen">
          <span class="device__nav"></span>
          <span class="device__hero-line device__hero-line--md"></span>
          <span class="device__hero-line"></span>
          <div class="device__cols device__cols--two">
            <span></span><span></span>
          </div>
        </div>
      </div>

      <div class="device device--phone">
        <div class="device__notch"></div>
        <div class="device__screen">
          <span class="device__nav"></span>
          <span class="device__hero-line device__hero-line--sm"></span>
          <span class="device__hero-line"></span>
          <div class="device__cols device__cols--one">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: `
    :host { display: block; }

    .trio {
      position: relative;
      display: grid;
      grid-template-columns: minmax(0, 1fr);
      gap: 0.75rem;
      min-width: 0;
      padding-bottom: 5.5rem;
      padding-right: 4.5rem;
    }

    @media (min-width: 40rem) {
      .trio { padding-bottom: 7rem; padding-right: 6rem; }
    }

    .device {
      position: relative;
      background: var(--c-pure);
      border: 1px solid var(--line);
      border-radius: 12px;
      box-shadow: var(--shadow-md);
      overflow: hidden;
      min-width: 0;
    }

    /* The tablet and phone sit behind the desktop, overlapping it slightly, so
       the three read as one composition rather than a row of boxes. */
    .device--tablet,
    .device--phone {
      position: absolute;
      bottom: 0;
      box-shadow: var(--shadow);
    }

    .device--tablet { right: 0; width: 46%; aspect-ratio: 3 / 4; z-index: 1; }
    .device--phone { right: 44%; width: 20%; min-width: 4.5rem; aspect-ratio: 1 / 2; border-radius: 10px; z-index: 2; }

    .device__chrome {
      display: flex;
      align-items: center;
      gap: 0.3rem;
      padding: 0.45rem 0.6rem;
      background: var(--c-sand);
      border-bottom: 1px solid var(--line);
    }

    .device__chrome--slim { justify-content: center; padding: 0.35rem 0.5rem; }

    .device__dot { width: 0.4rem; height: 0.4rem; border-radius: 50%; background: #d6d1ca; }

    .device__url {
      margin-left: 0.3rem;
      font-size: 0.58rem;
      color: var(--c-ink-muted);
      background: var(--c-white);
      border: 1px solid var(--line);
      border-radius: 3px;
      padding: 0.1rem 0.4rem;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .device__chrome--slim .device__url { margin-left: 0; max-width: 100%; }

    .device__screen { display: grid; gap: 0.4rem; padding: 0.6rem; }
    .device--phone .device__screen { padding: 0.75rem 0.4rem 0.4rem; }

    .device__notch {
      position: absolute;
      top: 0.22rem;
      left: 50%;
      transform: translateX(-50%);
      width: 34%;
      height: 0.28rem;
      border-radius: 0 0 3px 3px;
      background: var(--line);
      z-index: 1;
    }

    .device__nav { height: 0.4rem; border-radius: 2px; background: var(--c-line); }

    .device__hero-line { height: 0.45rem; border-radius: 2px; background: var(--c-line); }
    .device__hero-line--lg { height: 0.9rem; width: 72%; }
    .device__hero-line--md { height: 0.7rem; width: 78%; }
    .device__hero-line--sm { height: 0.55rem; width: 85%; }

    .device__cols { display: grid; gap: 0.35rem; margin-top: 0.2rem; }

    .device__cols > span {
      height: 1.6rem;
      border-radius: 3px;
      background: var(--c-sand);
      border: 1px solid var(--line);
    }

    .device__cols--two { grid-template-columns: repeat(2, minmax(0, 1fr)); }
    .device__cols--one { grid-template-columns: minmax(0, 1fr); }
    .device--phone .device__cols > span { height: 0.9rem; }

    /* Below this width the overlay composition would be cramped, so the devices
       simply stack and the reserved padding goes away. */
    @media (max-width: 30rem) {
      .trio { padding-bottom: 0; padding-right: 0; }
      .device--tablet,
      .device--phone { position: static; width: auto; aspect-ratio: auto; }
      .device--phone { max-width: 11rem; justify-self: center; }
    }
  `,
})
export class DeviceTrio {}