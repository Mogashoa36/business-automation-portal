package company.backend.domain;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * How far through the sales conversation a website enquiry is.
 *
 * <p>Stored on the enquiry so administrators can triage and report on incoming
 * work. The value is owned by the administration portal and is never accepted
 * from the public contact form, which only ever creates {@link #NEW} enquiries.
 */
public enum EnquiryStatus {

	/** Just received and not yet looked at. */
	NEW("New"),

	/** Being assessed, requirements being confirmed. */
	IN_PROGRESS("In progress"),

	/** The business has replied and a conversation is open. */
	CONTACTED("Contacted"),

	/** Handed over or delivered. */
	COMPLETED("Completed"),

	/** No longer active, kept for the record. */
	ARCHIVED("Archived");

	private final String displayName;

	EnquiryStatus(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Resolves a value supplied by the admin portal to an {@link EnquiryStatus}.
	 * @param value the enum name, as sent by the API
	 * @return the matching status, or {@link Optional#empty()} when unknown
	 */
	public static Optional<EnquiryStatus> findByValue(String value) {
		if (value == null || value.isBlank()) {
			return Optional.empty();
		}
		String candidate = value.trim();
		return Arrays.stream(values())
			.filter(status -> status.name().equalsIgnoreCase(candidate)
					|| status.displayName.toLowerCase(Locale.ROOT).equals(candidate.toLowerCase(Locale.ROOT)))
			.findFirst();
	}
}