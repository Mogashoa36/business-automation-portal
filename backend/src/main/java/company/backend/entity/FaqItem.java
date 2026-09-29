package company.backend.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * A question and answer published on the public website.
 *
 * <p>Answers are plain text. There is deliberately no rich text or HTML field
 * here, which removes any need to sanitise stored markup before it is rendered.
 */
@Entity
@Table(name = "faq_items", indexes = {
		@Index(name = "idx_faq_items_order", columnList = "displayOrder") })
public class FaqItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "question", nullable = false, length = 300)
	private String question;

	@Column(name = "answer", nullable = false, length = 4000)
	private String answer;

	/** Optional grouping, e.g. "Getting started". */
	@Column(name = "category", length = 80)
	private String category;

	/** Unpublished entries are visible to administrators only. */
	@Column(name = "published", nullable = false)
	private boolean published;

	@Column(name = "display_order", nullable = false)
	private int displayOrder;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected FaqItem() {
		// Required by JPA.
	}

	public FaqItem(String question, String answer, String category, boolean published, int displayOrder, Instant now) {
		this.question = question;
		this.answer = answer;
		this.category = category;
		this.published = published;
		this.displayOrder = displayOrder;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public Long getId() {
		return this.id;
	}

	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public int getDisplayOrder() {
		return this.displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Instant getCreatedAt() {
		return this.createdAt;
	}

	public Instant getUpdatedAt() {
		return this.updatedAt;
	}

	/** Called after every administrator edit so the audit trail stays accurate. */
	public void touch(Instant now) {
		this.updatedAt = now;
	}
}