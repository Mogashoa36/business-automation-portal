package company.backend.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.FaqResponse;
import company.backend.service.FaqService;

/**
 * Public, read only endpoint for the website FAQ.
 *
 * <p>Only published entries are returned, so drafts created in the admin
 * portal never appear on the marketing site.
 */
@RestController
@RequestMapping(path = "/api/faqs", produces = MediaType.APPLICATION_JSON_VALUE)
public class FaqController {

	private final FaqService faqService;

	public FaqController(FaqService faqService) {
		this.faqService = faqService;
	}

	@GetMapping
	public List<FaqResponse> findPublished() {
		return this.faqService.findPublished();
	}
}