package company.backend.dto;

import java.util.Arrays;
import java.util.List;

import company.backend.entity.PortfolioProject;

/** Public representation of a portfolio case study. */
public record PortfolioProjectResponse(Long id, String slug, String name, String industry, String summary,
		String problem, String solution, List<String> technologies, List<String> keyFeatures, List<String> results,
		String imageUrl, boolean featured, boolean sampleProject) {

	/**
	 * Maps a persisted project to its API representation, splitting the stored
	 * comma separated lists into real lists.
	 */
	public static PortfolioProjectResponse from(PortfolioProject project) {
		return new PortfolioProjectResponse(project.getId(), project.getSlug(), project.getName(), project.getIndustry(),
				project.getSummary(), project.getProblem(), project.getSolution(),
				splitToList(project.getTechnologies()), splitToList(project.getKeyFeatures()),
				splitToList(project.getResults()), project.getImageUrl(), project.isFeatured(),
				project.isSampleProject());
	}

	private static List<String> splitToList(String value) {
		if (value == null || value.isBlank()) {
			return List.of();
		}
		return Arrays.stream(value.split(","))
				.map(String::trim)
				.filter(item -> !item.isEmpty())
				.toList();
	}
}
