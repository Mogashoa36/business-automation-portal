package company.backend.service;

import java.util.List;

import company.backend.dto.FaqResponse;

/** Reads the published FAQs shown on the public website. */
public interface FaqService {

	/** @return published entries in display order; drafts are never included */
	List<FaqResponse> findPublished();
}