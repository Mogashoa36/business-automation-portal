package company.backend.service;

import java.util.List;

import company.backend.dto.PortfolioProjectResponse;

/**
 * Content management for portfolio case studies.
 *
 * <p>Every method here is only reachable from an authenticated admin endpoint.
 * Unlike {@link PortfolioProjectService}, these read and write drafts as well
 * as published entries.
 */
public interface AdminPortfolioService {

	/** Every case study, published or not, in display order. */
	List<PortfolioProjectResponse> findAll();

	PortfolioProjectResponse create(String actor, PortfolioProjectCommand command);

	PortfolioProjectResponse update(String actor, Long id, PortfolioProjectCommand command);

	PortfolioProjectResponse setPublished(String actor, Long id, boolean published);

	PortfolioProjectResponse reorder(String actor, List<Long> orderedIds);

	void delete(String actor, Long id);
}