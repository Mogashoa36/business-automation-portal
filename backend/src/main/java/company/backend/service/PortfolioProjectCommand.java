package company.backend.service;

import java.util.List;

/**
 * The fields an administrator can set on a portfolio case study.
 *
 * <p>A single command type keeps the create and update paths identical, which
 * removes a whole class of "the update forgot a field" bugs. List valued fields
 * arrive as arrays and are stored as the comma separated text the existing
 * entity already uses, so no persistence change is needed.
 */
public record PortfolioProjectCommand(String slug, String name, String industry, String summary, String problem,
		String solution, List<String> technologies, List<String> keyFeatures, List<String> results, String imageUrl,
		String category, boolean featured, boolean published, boolean sampleProject, Integer displayOrder) {
}