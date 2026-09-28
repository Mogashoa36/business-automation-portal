package company.backend.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.PortfolioProjectResponse;
import company.backend.service.PortfolioProjectService;

/**
 * Read only API for the portfolio case studies. The identifier is the URL
 * friendly slug so the website can link to {@code /portfolio/{slug}}.
 */
@RestController
@RequestMapping(path = "/api/portfolio", produces = MediaType.APPLICATION_JSON_VALUE)
public class PortfolioController {

	private final PortfolioProjectService portfolioProjectService;

	public PortfolioController(PortfolioProjectService portfolioProjectService) {
		this.portfolioProjectService = portfolioProjectService;
	}

	@GetMapping
	public List<PortfolioProjectResponse> findAll() {
		return this.portfolioProjectService.findAll();
	}

	@GetMapping("/featured")
	public List<PortfolioProjectResponse> findFeatured() {
		return this.portfolioProjectService.findFeatured();
	}

	@GetMapping("/{slug}")
	public PortfolioProjectResponse findBySlug(@PathVariable String slug) {
		return this.portfolioProjectService.findBySlug(slug);
	}
}
