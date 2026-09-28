package company.backend.exception;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import company.backend.domain.ServiceInterest;

/**
 * Raised when a contact request asks for a service the company does not offer.
 * Translated to HTTP 400 and reported back with the list of accepted values.
 */
public class InvalidServiceInterestException extends RuntimeException {

	private final String allowedValues;

	public InvalidServiceInterestException(String rejectedValue) {
		super("'" + rejectedValue + "' is not a valid service. Allowed values: " + acceptedValues() + ".");
		this.allowedValues = acceptedValues();
	}

	public String getAllowedValues() {
		return this.allowedValues;
	}

	private static String acceptedValues() {
		return Stream.of(ServiceInterest.values()).map(ServiceInterest::getDisplayName).collect(Collectors.joining(", "));
	}
}
