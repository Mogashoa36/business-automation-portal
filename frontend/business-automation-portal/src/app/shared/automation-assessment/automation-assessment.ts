import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ASSESSMENT_QUESTIONS, AssessmentResult, scoreAssessment } from '../../content/assessment.data';
import { PRIMARY_CTA } from '../../content/company';
import { Icon } from '../icon/icon';
import { Reveal } from '../reveal/reveal';

/**
 * The "Can your process be automated?" assessment.
 *
 * Six questions answered with real radio inputs, so the whole thing works with
 * a keyboard and is announced correctly by a screen reader. Selecting an option
 * updates the result immediately, so there is no submit button to hunt for.
 *
 * Nothing is stored or transmitted: the answers live in a signal in this
 * component and disappear with the page.
 */
@Component({
  selector: 'app-automation-assessment',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Icon, Reveal],
  templateUrl: './automation-assessment.html',
  styleUrl: './automation-assessment.scss',
})
export class AutomationAssessment {
  protected readonly questions = ASSESSMENT_QUESTIONS;
  protected readonly ctaLabel = PRIMARY_CTA.label;
  protected readonly ctaLink = PRIMARY_CTA.link;

  private readonly answersState = signal<Readonly<Record<string, string>>>({});

  protected readonly answers = this.answersState.asReadonly();
  protected readonly answered = computed(() => Object.keys(this.answersState()).length);

  /** Null until at least one answer is given, so nothing is implied up front. */
  protected readonly result = computed<AssessmentResult | null>(() =>
    this.answered() > 0 ? scoreAssessment(this.answersState()) : null,
  );

  protected select(questionId: string, option: string): void {
    this.answersState.update((current) => ({ ...current, [questionId]: option }));
  }

  protected reset(): void {
    this.answersState.set({});
  }
}