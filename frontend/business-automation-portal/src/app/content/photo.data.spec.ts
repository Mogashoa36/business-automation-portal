import { COMPANY_FACTS, PHOTO, hasPhoto } from './photo.data';

/**
 * Guards the rule that the site never implies a photograph exists when none
 * has been supplied. This is a content rule, so it belongs in a test rather
 * than only in a comment.
 */
describe('photo.data', () => {
  it('ships no photograph, so no placeholder image is mistaken for a real one', () => {
    expect(PHOTO.src).toBe('');
    expect(PHOTO.alt).toBe('');
    expect(hasPhoto()).toBe(false);
  });

  it('needs both a file and alt text before a photo counts as present', () => {
    // Alt text alone is not enough: an unlabelled image is worse than none.
    expect(hasPhoto({ src: '/images/studio.jpg', alt: '', caption: '' })).toBe(false);
    expect(hasPhoto({ src: '', alt: 'Our team', caption: '' })).toBe(false);
    expect(hasPhoto({ src: '/images/studio.jpg', alt: 'Our team in the studio', caption: '' })).toBe(true);
  });

  it('treats whitespace as empty, so a blank slot stays blank', () => {
    expect(hasPhoto({ src: '   ', alt: 'Our team', caption: '' })).toBe(false);
  });

  it('uses only real company facts', () => {
    expect(COMPANY_FACTS.length).toBeGreaterThan(0);
    for (const fact of COMPANY_FACTS) {
      expect(fact.value.trim().length).toBeGreaterThan(0);
    }
  });
});
