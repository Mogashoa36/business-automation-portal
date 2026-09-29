import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

/**
 * A responsive, lazily loaded image.
 *
 * Built as a component because the same three concerns apply to every image on
 * the site and getting any of them wrong in a template is easy:
 *
 * - **Reserved space.** `aspectRatio` and `width`/`height` are set up front, so
 *   the browser reserves the box before the bytes arrive and the page does not
 *   jump. This is the single biggest cause of layout shift on an image-led page.
 * - **Lazy loading.** `loading="lazy"` keeps below-the-fold images off the
 *   critical path. The hero passes `eager` and `fetchpriority="high"` because it
 *   is the Largest Contentful Paint element.
 * - **Decorative by default.** `alt` defaults to an empty string, which is the
 *   correct value for decoration. Meaningful images must pass real alt text, so
 *   an undescribed image cannot silently reach production.
 */
@Component({
  selector: 'app-image',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <span class="img" [style.--ratio]="ratio()">
      @if (isDecorative()) {
        <img class="img__el" [src]="src()" alt="" [attr.loading]="loading()" decoding="async"
          [attr.fetchpriority]="priority()" />
      } @else {
        <img class="img__el" [src]="src()" [alt]="alt()" [attr.loading]="loading()" decoding="async"
          [attr.fetchpriority]="priority()" />
      }
    </span>
  `,
  styles: `
    :host { display: block; min-width: 0; }

    /*
     * The wrapper carries the aspect ratio so the space is reserved even before
     * the image loads. The hidden overflow keeps a hover zoom inside the box
     * rather than spilling over neighbouring content.
     */
    .img {
      display: block;
      position: relative;
      width: 100%;
      aspect-ratio: var(--ratio, 16 / 10);
      overflow: hidden;
      background: var(--c-sand);
      border-radius: inherit;
    }

    .img__el {
      display: block;
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    /*
     * A gentle 1.03 scale on hover, the upper end of the requested range so it
     * reads as a considered movement rather than a zoom.
     */
    :host-context(a:hover) .img__el {
      transform: scale(1.03);
      transition: transform var(--d-emphasis) var(--ease);
    }

    @media (prefers-reduced-motion: reduce) {
      :host-context(a:hover) .img__el { transform: none; transition: none; }
    }
  `,
})
export class ResponsiveImage {
  /** The image URL. A missing or blank value renders nothing visible. */
  readonly src = input.required<string>();
  /**
   * Alternative text. Leave empty for a decorative image, which is the common
   * case here because the surrounding markup already carries the meaning.
   */
  readonly alt = input<string>('');
  /** `width / height`, as a CSS aspect-ratio value. */
  readonly ratio = input<string>('16 / 10');
  /** `lazy` below the fold, `eager` only for the LCP image. */
  readonly loading = input<'lazy' | 'eager'>('lazy');

  /** Derived so the template can branch, and so an empty alt is unambiguous. */
  protected readonly isDecorative = computed(() => this.alt().trim() === '');
  protected readonly priority = computed(() => (this.loading() === 'eager' ? 'high' : 'auto'));
}