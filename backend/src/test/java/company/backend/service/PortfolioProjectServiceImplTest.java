package company.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import company.backend.entity.PortfolioProject;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.PortfolioProjectRepository;

@ExtendWith(MockitoExtension.class)
class PortfolioProjectServiceImplTest {

	@Mock
	private PortfolioProjectRepository portfolioProjectRepository;

	private PortfolioProjectServiceImpl service;

	@BeforeEach
	void setUp() {
		this.service = new PortfolioProjectServiceImpl(this.portfolioProjectRepository);
	}

	@Test
	void returnsProjectsInDisplayOrder() {
		when(this.portfolioProjectRepository.findByPublishedTrueOrderByDisplayOrderAsc())
				.thenReturn(List.of(project("first", 1), project("second", 2)));

		assertThat(this.service.findAll()).extracting("slug").containsExactly("first", "second");
	}

	@Test
	void mapsCommaSeparatedValuesToLists() {
		when(this.portfolioProjectRepository.findBySlugAndPublishedTrue("first")).thenReturn(Optional.of(project("first", 1)));

		var response = this.service.findBySlug("first");
		assertThat(response.technologies()).containsExactly("Java", "Spring Boot", "PostgreSQL");
		assertThat(response.keyFeatures()).containsExactly("Workflow automation", "Audit trail");
		assertThat(response.results()).containsExactly("Outcome one", "Outcome two");
		assertThat(response.sampleProject()).isTrue();
	}

	@Test
	void returnsOnlyFeaturedProjects() {
		when(this.portfolioProjectRepository.findByPublishedTrueAndFeaturedTrueOrderByDisplayOrderAsc())
				.thenReturn(List.of());

		assertThat(this.service.findFeatured()).isEmpty();
	}

	@Test
	void failsWhenTheSlugIsUnknown() {
		when(this.portfolioProjectRepository.findBySlugAndPublishedTrue("missing")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> this.service.findBySlug("missing")).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("missing");
	}

	private PortfolioProject project(String slug, int displayOrder) {
		return new PortfolioProject(slug, "Name " + slug, "Logistics", "Summary", "Problem", "Solution",
				"Java, Spring Boot, PostgreSQL", "Workflow automation, Audit trail", "Outcome one, Outcome two", null, true,
				true, displayOrder);
	}
}
