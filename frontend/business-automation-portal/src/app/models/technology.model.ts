import { IconName } from './icon.model';

/** A technology the company works with, with a short plain-English note. */
export interface TechnologyItem {
  readonly name: string;
  readonly note: string;
}

/** A group of related technologies shown on the technology page. */
export interface TechnologyGroup {
  readonly id: string;
  readonly title: string;
  readonly icon: IconName;
  readonly description: string;
  readonly items: readonly TechnologyItem[];
}
