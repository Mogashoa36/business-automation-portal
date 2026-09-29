package company.backend.service;

import java.util.List;

import company.backend.dto.PortfolioProjectResponse;

/**
 * Read operations for the portfolio case studies shown on the website.
 */
public interface PortfolioProjectService {

	/** @return every case study, in display order */
	List<PortfolioProjectResponse> findAll();

	/** @return the case studies promoted on the home page */
	List<PortfolioProjectResponse> findFeatured();

	/**
	 * @param slug the URL friendly project identifier
	 * @return the matching case study
	 * @throws company.backend.exception.ResourceNotFoundException if no project
	 * uses the given slug
	 */
	PortfolioProjectResponse findBySlug(String slug);

	/**
	 * @return the distinct categories that have at least one published case
	 * study, used to build the public filter chips
	 */
	List<String> findPublishedCategories();
}
