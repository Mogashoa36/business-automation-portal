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
 * An audit trail entry for a significant administrative action.
 *
 * <p>Only the acting administrator, the action, the affected resource and the
 * outcome are recorded. Passwords, tokens and any other secret are never
 * written here, and the activity API never returns them.
 */
@Entity
@Table(name = "activity_logs", indexes = {
		@Index(name = "idx_activity_logs_occurred_at", columnList = "occurredAt") })
public class ActivityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "occurred_at", nullable = false)
	private Instant occurredAt;

	/** Username of the administrator who performed the action. */
	@Column(name = "actor", nullable = false, length = 60)
	private String actor;

	/** Short action label, e.g. "PORTFOLIO_UPDATED". */
	@Column(name = "action", nullable = false, length = 60)
	private String action;

	/** The affected resource, e.g. "PortfolioProject:12". */
	@Column(name = "resource", length = 160)
	private String resource;

	/** SUCCESS or FAILURE, so a rejected attempt is still visible. */
	@Column(name = "result", nullable = false, length = 20)
	private String result;

	/** Extra context. Must never contain credentials or secrets. */
	@Column(name = "detail", length = 400)
	private String detail;

	protected ActivityLog() {
		// Required by JPA.
	}

	public ActivityLog(String actor, String action, String resource, String result, String detail, Instant now) {
		this.actor = actor;
		this.action = action;
		this.resource = resource;
		this.result = result;
		this.detail = detail;
		this.occurredAt = now;
	}

	public Long getId() {
		return this.id;
	}

	public Instant getOccurredAt() {
		return this.occurredAt;
	}

	public String getActor() {
		return this.actor;
	}

	public String getAction() {
		return this.action;
	}

	public String getResource() {
		return this.resource;
	}

	public String getResult() {
		return this.result;
	}

	public String getDetail() {
		return this.detail;
	}
}