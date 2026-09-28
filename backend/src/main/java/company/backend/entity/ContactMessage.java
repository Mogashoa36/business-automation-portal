package company.backend.entity;

import java.time.Instant;

import company.backend.domain.ServiceInterest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * A message submitted through the website contact form.
 *
 * <p>Only fields the website actually collects are persisted. No credentials
 * or other sensitive data are ever stored here.
 */
@Entity
@Table(name = "contact_messages", indexes = {
		@Index(name = "idx_contact_messages_reference", columnList = "reference", unique = true),
		@Index(name = "idx_contact_messages_created_at", columnList = "createdAt") })
public class ContactMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "full_name", nullable = false, length = 120)
	private String fullName;

	@Column(name = "company", length = 120)
	private String company;

	@Column(name = "email", nullable = false, length = 180)
	private String email;

	@Column(name = "phone", length = 40)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(name = "service", nullable = false, length = 60)
	private ServiceInterest service;

	@Column(name = "message", nullable = false, length = 4000)
	private String message;

	/** Human readable reference quoted back to the sender, e.g. {@code CM-20260927-7F3K}. */
	@Column(name = "reference", nullable = false, unique = true, length = 40)
	private String reference;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected ContactMessage() {
		// Required by JPA.
	}

	public ContactMessage(String fullName, String company, String email, String phone, ServiceInterest service,
			String message, String reference, Instant createdAt) {
		this.fullName = fullName;
		this.company = company;
		this.email = email;
		this.phone = phone;
		this.service = service;
		this.message = message;
		this.reference = reference;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return this.id;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getCompany() {
		return this.company;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPhone() {
		return this.phone;
	}

	public ServiceInterest getService() {
		return this.service;
	}

	public String getMessage() {
		return this.message;
	}

	public String getReference() {
		return this.reference;
	}

	public Instant getCreatedAt() {
		return this.createdAt;
	}
}
