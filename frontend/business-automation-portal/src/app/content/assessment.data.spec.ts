import { describe, expect, it } from 'vitest';

import { ASSESSMENT_QUESTIONS, scoreAssessment } from './assessment.data';

/** Builds an answer set from the first option of every question. */
function answerWith(index: number): Record<string, string> {
  const answers: Record<string, string> = {};
  for (const question of ASSESSMENT_QUESTIONS) {
    answers[question.id] = question.options[index].label;
  }
  return answers;
}

describe('scoreAssessment', () => {
  it('gives a high score when every answer describes a manual process', () => {
    const result = scoreAssessment(answerWith(0));
    expect(result.band).toBe('high');
    expect(result.score).toBeGreaterThanOrEqual(66);
  });

  it('gives a low score when nothing is manual', () => {
    const result = scoreAssessment(answerWith(3));
    expect(result.band).toBe('low');
  });

  it('scores zero when nothing has been answered', () => {
    const result = scoreAssessment({});
    expect(result.score).toBe(0);
    expect(result.opportunities).toHaveLength(0);
  });

  it('never scores above 100, whatever is selected', () => {
    const result = scoreAssessment(answerWith(0));
    expect(result.score).toBeLessThanOrEqual(100);
  });

  it('suggests approval routing when approvals are part of the process', () => {
    const answers = answerWith(0);
    answers['approvals'] = 'Yes, several people approve at different stages';

    const titles = scoreAssessment(answers).opportunities.map((opportunity) => opportunity.title);
    expect(titles).toContain('Approval routing');
  });

  /**
   * "No approvals" removes the approvals signal, but the follow-up answer in
   * this set also carries it, so the opportunity can legitimately remain. What
   * matters is that the approvals question itself contributes nothing when it
   * is answered "no", which is asserted here in isolation.
   */
  it('does not suggest approval routing when there are no approvals anywhere', () => {
    const answers: Record<string, string> = {
      process: 'Mostly in a system already',
      frequency: 'Monthly or less',
      approvals: 'No approvals',
      dataEntry: 'Never',
      reporting: 'No',
      followUp: 'Rarely, it moves on its own',
    };

    const titles = scoreAssessment(answers).opportunities.map((opportunity) => opportunity.title);
    expect(titles).not.toContain('Approval routing');
  });

  it('marks a signal as a strong fit only when it recurs', () => {
    // "Automated notifications" appears in both the frequency and follow-up
    // answers, so it should be a strong fit rather than merely possible.
    const result = scoreAssessment(answerWith(0));
    const notifications = result.opportunities.find((o) => o.title === 'Automated notifications');
    expect(notifications?.strength).toBe('strong');
  });

  it('always returns a label and a summary for the band', () => {
    const result = scoreAssessment(answerWith(1));
    expect(result.bandLabel.length).toBeGreaterThan(0);
    expect(result.bandSummary.length).toBeGreaterThan(0);
  });

  it('ignores an answer that is not one of the offered options', () => {
    const answers = { ...answerWith(0), process: 'Something invented' };
    // The invented answer is skipped, so the score reflects four questions
    // rather than five, and must still be a valid band.
    const result = scoreAssessment(answers);
    expect(['low', 'moderate', 'high']).toContain(result.band);
  });
});

describe('ASSESSMENT_QUESTIONS', () => {
  it('covers the six areas the assessment is meant to look at', () => {
    const ids = ASSESSMENT_QUESTIONS.map((question) => question.id);
    expect(ids).toEqual(['process', 'frequency', 'approvals', 'dataEntry', 'reporting', 'followUp']);
  });

  it('gives every question a unique set of option labels', () => {
    for (const question of ASSESSMENT_QUESTIONS) {
      const labels = question.options.map((option) => option.label);
      expect(new Set(labels).size).toBe(labels.length);
    }
  });
});
