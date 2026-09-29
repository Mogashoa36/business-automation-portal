package company.backend.dto;

import company.backend.entity.FaqItem;

/**
 * A FAQ entry.
 *
 * <p>Public and admin shapes are the same record. Only published entries are
 * ever returned from the public endpoint, so the `published` flag is simply
 * always `true` for a visitor and meaningful only in the admin portal. Sharing
 * one record avoids two mappings that could drift apart.
 *
 * <p>The answer is plain text, so the Angular template renders it with
 * interpolation and there is no stored HTML to sanitise.
 */
public record FaqResponse(Long id, String question, String answer, String category, boolean published,
		int displayOrder) {

	public static FaqResponse from(company.backend.entity.FaqItem item) {
		return new FaqResponse(item.getId(), item.getQuestion(), item.getAnswer(), item.getCategory(),
				item.isPublished(), item.getDisplayOrder());
	}
}