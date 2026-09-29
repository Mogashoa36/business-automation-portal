package company.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.dto.PortfolioProjectResponse;
import company.backend.entity.PortfolioProject;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.PortfolioProjectRepository;

@Service
@Transactional(readOnly = true)
public class PortfolioProjectServiceImpl implements PortfolioProjectService {

	private final PortfolioProjectRepository portfolioProjectRepository;

	public PortfolioProjectServiceImpl(PortfolioProjectRepository portfolioProjectRepository) {
		this.portfolioProjectRepository = portfolioProjectRepository;
	}

	@Override
	public List<PortfolioProjectResponse> findAll() {
		return this.portfolioProjectRepository.findByPublishedTrueOrderByDisplayOrderAsc().stream().map(PortfolioProjectResponse::from)
				.toList();
	}

	@Override
	public List<PortfolioProjectResponse> findFeatured() {
		return this.portfolioProjectRepository.findByPublishedTrueAndFeaturedTrueOrderByDisplayOrderAsc().stream()
				.map(PortfolioProjectResponse::from).toList();
	}

	@Override
	public PortfolioProjectResponse findBySlug(String slug) {
		PortfolioProject project = this.portfolioProjectRepository.findBySlugAndPublishedTrue(slug)
				.orElseThrow(() -> new ResourceNotFoundException("No portfolio project found with slug '" + slug + "'."));
		return PortfolioProjectResponse.from(project);
	}

	/**
	 * Categories that actually have a published case study. Drives the public
	 * filter chips so the website never offers a filter that returns nothing.
	 */
	@Override
	public List<String> findPublishedCategories() {
		return this.portfolioProjectRepository.findPublishedCategories();
	}
}
