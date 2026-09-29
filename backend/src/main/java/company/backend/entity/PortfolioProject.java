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

	/**
	 * Whether the case study appears on the public website. Drafts stay in the
	 * database and are visible to administrators only, which is what makes the
	 * publish/unpublish control meaningful.
	 */
	@Column(name = "published", nullable = false)
	private boolean published = true;

	/**
	 * Optional filter label, e.g. "Workflow Management". The public portfolio
	 * filter reads this, and the existing {@code industry} field is kept
	 * untouched so the public contract does not change.
	 */
	@Column(name = "category", length = 80)
	private String category;

	@Column(name = "display_order", nullable = false)
	private int displayOrder;

	@Column(name = "created_at", nullable = false)
	private java.time.Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private java.time.Instant updatedAt;

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
		this.published = true;
		this.createdAt = java.time.Instant.now();
		this.updatedAt = this.createdAt;
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

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public void setTechnologies(String technologies) {
		this.technologies = technologies;
	}

	public void setKeyFeatures(String keyFeatures) {
		this.keyFeatures = keyFeatures;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setSampleProject(boolean sampleProject) {
		this.sampleProject = sampleProject;
	}

	public java.time.Instant getCreatedAt() {
		return this.createdAt;
	}

	public java.time.Instant getUpdatedAt() {
		return this.updatedAt;
	}

	/** Called after every administrator edit so the record stays accurate. */
	public void touch(java.time.Instant now) {
		this.updatedAt = now;
	}
}
