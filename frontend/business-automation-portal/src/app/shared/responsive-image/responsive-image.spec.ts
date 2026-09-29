import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';

import { ResponsiveImage } from './responsive-image';

/** Host used to drive the inputs of a signal based component. */
@Component({
  imports: [ResponsiveImage],
  template: '<app-image [src]="src" [alt]="alt" [ratio]="ratio" [loading]="loading" />',
})
class Host {
  src = '/api/images/abc.png';
  alt = '';
  ratio = '16 / 10';
  loading: 'lazy' | 'eager' = 'lazy';
}

describe('ResponsiveImage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [Host] }).compileComponents();
  });

  /** The rendered img element. */
  function render(): HTMLImageElement {
    const fixture = TestBed.createComponent(Host);
    fixture.detectChanges();
    return fixture.nativeElement.querySelector('img') as HTMLImageElement;
  }

  it('renders the supplied source', () => {
    expect(render().getAttribute('src')).toBe('/api/images/abc.png');
  });

  it('uses an empty alt for a decorative image, which is the safe default', () => {
    // An empty alt is the correct value for decoration. The default means a
    // forgotten alt attribute can never read out a filename.
    expect(render().getAttribute('alt')).toBe('');
  });

  it('passes real alt text through for a meaningful image', () => {
    const fixture = TestBed.createComponent(Host);
    fixture.componentInstance.alt = 'A workflow showing an approved purchase request';
    fixture.detectChanges();

    expect((fixture.nativeElement.querySelector('img') as HTMLImageElement).getAttribute('alt')).toBe(
      'A workflow showing an approved purchase request',
    );
  });

  it('lazy loads by default, keeping below the fold images off the critical path', () => {
    expect(render().getAttribute('loading')).toBe('lazy');
  });

  it('marks an eager image as high priority, for the LCP element', () => {
    const fixture = TestBed.createComponent(Host);
    fixture.componentInstance.loading = 'eager';
    fixture.detectChanges();

    const img = fixture.nativeElement.querySelector('img') as HTMLImageElement;
    expect(img.getAttribute('loading')).toBe('eager');
    expect(img.getAttribute('fetchpriority')).toBe('high');
  });

  it('does not prioritise a lazy image', () => {
    expect(render().getAttribute('fetchpriority')).toBe('auto');
  });

  it('reserves the space before the image loads, which is what prevents layout shift', () => {
    // The ratio lives on the wrapper, not the img, so the box exists even while
    // the image is still downloading.
    const wrapper = (render() as HTMLElement).parentElement as HTMLElement;
    expect(wrapper.style.getPropertyValue('--ratio')).toBe('16 / 10');
  });

  it('always requests async decoding', () => {
    expect(render().getAttribute('decoding')).toBe('async');
  });
});
