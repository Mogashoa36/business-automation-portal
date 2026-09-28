package company.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import company.backend.entity.PortfolioProject;

public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, Long> {

	List<PortfolioProject> findAllByOrderByDisplayOrderAsc();

	List<PortfolioProject> findByFeaturedTrueOrderByDisplayOrderAsc();

	Optional<PortfolioProject> findBySlug(String slug);
}
