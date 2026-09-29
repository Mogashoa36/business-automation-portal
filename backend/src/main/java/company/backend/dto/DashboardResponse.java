package company.backend.dto;

import java.util.List;

/**
 * Admin dashboard summary.
 *
 * <p>Every number here is a real count read from the database. Nothing is
 * hard coded or estimated, so an empty database correctly reports zeros rather
 * than invented figures.
 */
public record DashboardResponse(Counts counts, List<ActivityLogResponse> recentActivity) {

	/**
	 * @param services and {@code solutions} are counted from the static
	 * marketing content the website actually ships, so the figure matches what
	 * a visitor sees
	 * @param publishedContent the total number of published items a visitor can
	 * currently read
	 */
	public record Counts(long portfolioProjects, long publishedProjects, long draftProjects, int services,
			int solutions, long faqs, long publishedFaqs, long enquiries, long newEnquiries, long activeEnquiries,
			long administrators, long publishedContent) {
	}
}