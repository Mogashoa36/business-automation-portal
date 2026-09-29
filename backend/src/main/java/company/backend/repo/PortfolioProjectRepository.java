package company.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import company.backend.entity.PortfolioProject;

public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, Long> {

	List<PortfolioProject> findAllByOrderByDisplayOrderAsc();

	List<PortfolioProject> findByFeaturedTrueOrderByDisplayOrderAsc();

	Optional<PortfolioProject> findBySlug(String slug);

	/*
	 * Public website queries. Unpublished drafts must never reach the marketing
	 * site, so every public read goes through one of these.
	 */
	List<PortfolioProject> findByPublishedTrueOrderByDisplayOrderAsc();

	List<PortfolioProject> findByPublishedTrueAndFeaturedTrueOrderByDisplayOrderAsc();

	Optional<PortfolioProject> findBySlugAndPublishedTrue(String slug);

	/** Categories that actually have a published case study, for the filter chips. */
	@Query("select distinct p.category from PortfolioProject p "
			+ "where p.published = true and p.category is not null and trim(p.category) <> '' order by p.category")
	List<String> findPublishedCategories();

	long countByPublishedTrue();
}
