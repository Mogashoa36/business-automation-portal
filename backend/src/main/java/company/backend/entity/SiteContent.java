package company.backend.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An editable, non sensitive piece of website content, such as a page title or
 * meta description.
 *
 * <p>Keyed by a stable slug (for example {@code page:home}). The frontend keeps
 * its compiled in values as the fallback, so an absent or blank row simply
 * means "use the default" and the page can never be left without a title.
 *
 * <p>Only presentational text belongs here. Application configuration such as
 * database credentials, signing keys or environment variables is deliberately
 * not editable through the admin UI and is never stored in this table.
 */
@Entity
@Table(name = "site_contents")
public class SiteContent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Stable key, e.g. {@code seo:home} or {@code setting:businessHours}. */
	@Column(name = "content_key", nullable = false, unique = true, length = 120)
	private String contentKey;

	@Column(name = "content_value", length = 2000)
	private String contentValue;

	@Column(name = "updated_by", length = 60)
	private String updatedBy;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected SiteContent() {
		// Required by JPA.
	}

	public SiteContent(String contentKey, String contentValue, String updatedBy, Instant now) {
		this.contentKey = contentKey;
		this.contentValue = contentValue;
		this.updatedBy = updatedBy;
		this.updatedAt = now;
	}

	public Long getId() {
		return this.id;
	}

	public String getContentKey() {
		return this.contentKey;
	}

	public String getContentValue() {
		return this.contentValue;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public Instant getUpdatedAt() {
		return this.updatedAt;
	}

	public void update(String contentValue, String updatedBy, Instant now) {
		this.contentValue = contentValue;
		this.updatedBy = updatedBy;
		this.updatedAt = now;
	}
}