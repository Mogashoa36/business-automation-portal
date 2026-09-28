package company.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * A portfolio case study.
 *
 * <p>Rows flagged with {@code sampleProject} are illustrative placeholders that
 * describe a realistic scenario. They are never presented as work delivered for
 * a real client - the website renders an explicit "sample project" label for
 * them. Replace or add rows as real projects become available.
 */
@Entity
@Table(name = "portfolio_projects", indexes = {
		@Index(name = "idx_portfolio_projects_slug", columnList = "slug", unique = true) })
public class PortfolioProject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** URL friendly identifier, e.g. {@code approval-automation}. */
	@Column(name = "slug", nullable = false, unique = true, length = 80)
	private String slug;

	@Column(name = "name", nullable = false, length = 160)
	private String name;

	@Column(name = "industry", nullable = false, length = 80)
	private String industry;

	@Column(name = "summary", nullable = false, length = 400)
	private String summary;

	@Column(name = "problem", nullable = false, length = 4000)
	private String problem;

	@Column(name = "solution", nullable = false, length = 4000)
	private String solution;

	/** Comma separated list of technologies used, exposed as a list by the API. */
	@Column(name = "technologies", nullable = false, length = 500)
	private String technologies;

	/** Comma separated list of key features, exposed as a list by the API. */
	@Column(name = "key_features", nullable = false, length = 1000)
	private String keyFeatures;

	/** Comma separated list of outcomes, exposed as a list by the API. */
	@Column(name = "results", nullable = false, length = 1000)
	private String results;

	/** Optional screenshot or project image URL. The UI falls back to a branded placeholder. */
	@Column(name = "image_url", length = 500)
	private String imageUrl;

	/** Whether the project is promoted on the home page. */
	@Column(name = "featured", nullable = false)
	private boolean featured;

	/** True for illustrative placeholder content that is not a real client project. */
	@Column(name = "sample_project", nullable = false)
	private boolean sampleProject;

	@Column(name = "display_order", nullable = false)
	private int displayOrder;

	protected PortfolioProject() {
		// Required by JPA.
	}

	public PortfolioProject(String slug, String name, String industry, String summary, String problem, String solution,
			String technologies, String keyFeatures, String results, String imageUrl, boolean featured,
			boolean sampleProject, int displayOrder) {
		this.slug = slug;
		this.name = name;
		this.industry = industry;
		this.summary = summary;
		this.problem = problem;
		this.solution = solution;
		this.technologies = technologies;
		this.keyFeatures = keyFeatures;
		this.results = results;
		this.imageUrl = imageUrl;
		this.featured = featured;
		this.sampleProject = sampleProject;
		this.displayOrder = displayOrder;
	}

	public Long getId() {
		return this.id;
	}

	public String getSlug() {
		return this.slug;
	}

	public String getName() {
		return this.name;
	}

	public String getIndustry() {
		return this.industry;
	}

	public String getSummary() {
		return this.summary;
	}

	public String getProblem() {
		return this.problem;
	}

	public String getSolution() {
		return this.solution;
	}

	public String getTechnologies() {
		return this.technologies;
	}

	public String getKeyFeatures() {
		return this.keyFeatures;
	}

	public String getResults() {
		return this.results;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public boolean isFeatured() {
		return this.featured;
	}

	public boolean isSampleProject() {
		return this.sampleProject;
	}

	public int getDisplayOrder() {
		return this.displayOrder;
	}
}
