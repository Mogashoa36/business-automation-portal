package company.backend.domain;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The areas of the business a website enquiry can be about.
 *
 * <p>Keeping the allowed values in an enum means the API can never accept a
 * service request that the website does not offer.
 */
public enum ServiceInterest {

	BUSINESS_PROCESS_AUTOMATION("Business Process Automation"),
	CUSTOM_BUSINESS_APPLICATION("Custom Business Application"),
	WEBSITE_DEVELOPMENT("Website Development"),
	WORKFLOW_MANAGEMENT("Workflow Management"),
	OPERATIONS_MANAGEMENT("Operations Management"),
	EMPLOYEE_MANAGEMENT("Employee Management"),
	PROCUREMENT("Procurement"),
	REPORTING_DASHBOARDS("Reporting & Dashboards"),
	OTHER("Other");

	private final String displayName;

	ServiceInterest(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Resolves a raw request value (either the enum name or the display name) to
	 * a {@link ServiceInterest}.
	 * @param value the raw value received from a client
	 * @return the matching interest, or {@link Optional#empty()} when unknown
	 */
	public static Optional<ServiceInterest> findByValue(String value) {
		if (value == null || value.isBlank()) {
			return Optional.empty();
		}
		String candidate = value.trim();
		return Arrays.stream(values())
				.filter(interest -> interest.name().equalsIgnoreCase(candidate)
						|| interest.displayName.toLowerCase(Locale.ROOT).equals(candidate.toLowerCase(Locale.ROOT)))
				.findFirst();
	}
}
