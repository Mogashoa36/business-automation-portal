import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { FaqItem } from '../../models/faq-item.model';
import { FaqService } from '../../services/faq.service';
import { Icon } from '../icon/icon';
import { Reveal } from '../reveal/reveal';

/**
 * Frequently asked questions.
 *
 * Built with `<details>`/`<summary>` rather than a scripted accordion: the
 * markup works before JavaScript arrives, is announced correctly, is keyboard
 * operable for free, and each panel is exposed to a search engine without any
 * extra work. Only one panel is open at a time, enforced by the `name`
 * attribute, which also gives the familiar native behaviour.
 */
@Component({
  selector: 'app-faq-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Icon, Reveal],
  templateUrl: './faq-section.html',
  styleUrl: './faq-section.scss',
})
export class FaqSection {
  private readonly faqService = inject(FaqService);

  protected readonly faqs = signal<readonly FaqItem[]>([]);
  protected readonly loading = signal(true);
  protected readonly available = signal(false);

  constructor() {
    this.faqService.list().subscribe({
      next: (items) => {
        this.faqs.set(items);
        this.available.set(items.length > 0);
        this.loading.set(false);
      },
      error: () => {
        // The service already falls back to an empty list, so the section is
        // simply hidden rather than showing an error to a visitor.
        this.loading.set(false);
      },
    });
  }
}