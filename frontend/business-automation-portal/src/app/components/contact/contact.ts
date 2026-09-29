import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { COMPANY } from '../../content/company';
import { SERVICES } from '../../content/services.data';
import { SOLUTIONS } from '../../content/solutions.data';
import {
  ContactMessageResponse,
  SERVICE_OPTIONS,
  ServiceOption,
} from '../../models/contact-message.model';
import { ContactService, describeApiError } from '../../services/contact.service';
import { SeoService } from '../../services/seo.service';
import { Icon } from '../../shared/icon/icon';
import { PageHero } from '../../shared/page-hero/page-hero';
import { Reveal } from '../../shared/reveal/reveal';

type SubmitState = 'idle' | 'submitting' | 'success' | 'error';

@Component({
  selector: 'app-contact',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, Icon, PageHero, Reveal],
  templateUrl: './contact.html',
  styleUrl: './contact.scss',
})
export class Contact {
  private readonly formBuilder = inject(FormBuilder);
  private readonly contactService = inject(ContactService);
  private readonly seo = inject(SeoService);

  protected readonly company = COMPANY;
  /** Phone number in a `tel:` friendly format (no spaces). */
  protected readonly dialNumber = COMPANY.phone.replace(/\s/g, '');
  protected readonly serviceOptions: readonly ServiceOption[] = SERVICE_OPTIONS;
  protected readonly services = SERVICES;
  protected readonly solutions = SOLUTIONS;

  protected readonly state = signal<SubmitState>('idle');
  protected readonly statusMessage = signal('');
  protected readonly receipt = signal<ContactMessageResponse | null>(null);
  /** Field level messages returned by the API, shown next to the inputs. */
  protected readonly serverFieldErrors = signal<Record<string, string>>({});

  protected readonly form = this.formBuilder.nonNullable.group({
    fullName: ['', [Validators.required, Validators.maxLength(120)]],
    company: ['', [Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(180)]],
    phone: ['', [Validators.maxLength(40)]],
    service: ['', [Validators.required]],
    message: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(4000)]],
  });

  constructor() {
    this.seo.update({
      title: 'Contact | Start a Business Automation Project with Bizflow Technologies',
      description:
        'Tell Bizflow Technologies which business process is slowing you down. Request a consultation on business process automation, custom applications or website development.',
      path: '/contact',
    });
  }

  protected hasError(control: keyof typeof this.form.controls): boolean {
    const field = this.form.controls[control];
    return field.invalid && (field.dirty || field.touched || this.serverFieldErrors()[control] !== undefined);
  }

  protected errorFor(control: keyof typeof this.form.controls): string {
    const serverError = this.serverFieldErrors()[control];
    if (serverError) {
      return serverError;
    }

    const field = this.form.controls[control];
    if (!field.errors) {
      return '';
    }
    if (field.errors['required']) {
      return `${this.labelFor(control)} is required.`;
    }
    if (field.errors['email']) {
      return 'Enter a valid email address, for example name@company.co.za.';
    }
    if (field.errors['minlength']) {
      const required = field.errors['minlength'].requiredLength as number;
      return `Please use at least ${required} characters.`;
    }
    if (field.errors['maxlength']) {
      const limit = field.errors['maxlength'].requiredLength as number;
      return `Please keep this under ${limit} characters.`;
    }
    return 'Please check this field.';
  }

  private labelFor(control: keyof typeof this.form.controls): string {
    return {
      fullName: 'Full name',
      company: 'Company',
      email: 'Email address',
      phone: 'Phone number',
      service: 'A service',
      message: 'Message',
    }[control];
  }

  protected prefill(service: string): void {
    this.form.controls.service.setValue(service);
    this.form.controls.service.markAsTouched();
  }

  protected submit(): void {
    this.serverFieldErrors.set({});

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.state.set('error');
      this.statusMessage.set('Please correct the highlighted fields and try again.');
      return;
    }

    this.state.set('submitting');
    this.statusMessage.set('Sending your enquiry...');

    this.contactService.submit(this.form.getRawValue()).subscribe({
      next: (receipt) => {
        this.receipt.set(receipt);
        this.state.set('success');
        this.statusMessage.set(receipt.confirmation);
        this.form.reset({ fullName: '', company: '', email: '', phone: '', service: '', message: '' });
      },
      error: (error: unknown) => {
        const { message, fieldErrors } = describeApiError(error);
        this.serverFieldErrors.set(fieldErrors);
        this.state.set('error');
        this.statusMessage.set(message);
        if (Object.keys(fieldErrors).length) {
          this.form.markAllAsTouched();
        }
      },
    });
  }
}
