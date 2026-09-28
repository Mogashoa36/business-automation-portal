package company.backend.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import company.backend.entity.PortfolioProject;
import company.backend.repo.PortfolioProjectRepository;

/**
 * End to end tests for the read only portfolio API, including the 404 contract
 * used by the website when a project slug does not exist.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PortfolioControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PortfolioProjectRepository portfolioProjectRepository;

	@BeforeEach
	void setUp() {
		this.portfolioProjectRepository.deleteAll();
		this.portfolioProjectRepository
				.save(probe("approval-automation", "Approval Automation Platform", "Professional Services", 1, true));
		this.portfolioProjectRepository
				.save(probe("procurement-workflow", "Procurement Workflow System", "Manufacturing", 2, false));
	}

	@Test
	void returnsAllProjectsInDisplayOrder() throws Exception {
		mockMvc.perform(get("/api/portfolio")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].slug", is("approval-automation")))
				.andExpect(jsonPath("$[1].slug", is("procurement-workflow")));
	}

	@Test
	void splitsStoredListsAndExposesSampleFlag() throws Exception {
		mockMvc.perform(get("/api/portfolio/approval-automation")).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Approval Automation Platform")))
				.andExpect(jsonPath("$.industry", is("Professional Services")))
				.andExpect(jsonPath("$.sampleProject", is(true)))
				.andExpect(jsonPath("$.featured", is(true)))
				.andExpect(jsonPath("$.imageUrl").doesNotExist())
				// Comma separated columns are exposed as real JSON arrays.
				.andExpect(jsonPath("$.technologies", hasSize(3))).andExpect(jsonPath("$.technologies[0]", is("Java")))
				.andExpect(jsonPath("$.keyFeatures", hasSize(2))).andExpect(jsonPath("$.keyFeatures[0]", is("Feature one")))
				.andExpect(jsonPath("$.results", hasSize(2))).andExpect(jsonPath("$.results[1]", is("Outcome two")));
	}

	@Test
	void returnsOnlyFeaturedProjects() throws Exception {
		mockMvc.perform(get("/api/portfolio/featured")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].slug", is("approval-automation")));
	}

	@Test
	void returnsNotFoundForUnknownSlug() throws Exception {
		mockMvc.perform(get("/api/portfolio/does-not-exist")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status", is(404)))
				.andExpect(jsonPath("$.message", is("No portfolio project found with slug 'does-not-exist'.")));
	}

	private PortfolioProject probe(String slug, String name, String industry, int displayOrder, boolean featured) {
		return new PortfolioProject(slug, name, industry, "A short summary of the project.", "The problem.",
				"The solution.", "Java, Spring Boot, PostgreSQL", "Feature one, Feature two", "Outcome one, Outcome two", null,
				featured, true, displayOrder);
	}
}
