package company.backend.controller.admin;

import java.util.List;

import company.backend.service.PortfolioProjectCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Create or update payload for a portfolio case study.
 *
 * <p>Bean validation rejects an oversized or missing field before it reaches
 * the service, and the service then applies the domain rules such as slug
 * uniqueness.
 */
public record PortfolioProjectRequest(

		@Size(max = 80, message = "Slug must be 80 characters or fewer")
		String slug,

		@NotBlank(message = "Project name is required")
		@Size(max = 160, message = "Project name must be 160 characters or fewer")
		String name,

		@NotBlank(message = "Industry is required")
		@Size(max = 80, message = "Industry must be 80 characters or fewer")
		String industry,

		@NotBlank(message = "A short description is required")
		@Size(max = 400, message = "Short description must be 400 characters or fewer")
		String summary,

		@NotBlank(message = "A challenge description is required")
		@Size(max = 4000, message = "Challenge must be 4000 characters or fewer")
		String problem,

		@NotBlank(message = "A solution description is required")
		@Size(max = 4000, message = "Solution must be 4000 characters or fewer")
		String solution,

		List<String> technologies,

		List<String> keyFeatures,

		List<String> results,

		@Size(max = 500, message = "Image URL must be 500 characters or fewer")
		String imageUrl,

		@Size(max = 80, message = "Category must be 80 characters or fewer")
		String category,

		boolean featured,

		boolean published,

		boolean sampleProject,

		@Size(min = 0, max = 10000, message = "Display order must be between 0 and 10000")
		Integer displayOrder) {

	/** Maps the validated payload onto the service command. */
	public PortfolioProjectCommand toCommand() {
		return new PortfolioProjectCommand(slug, name, industry, summary, problem, solution, technologies, keyFeatures,
				results, imageUrl, category, featured, published, sampleProject, displayOrder);
	}
}