package company.backend.exception;

/**
 * Raised when a request cannot be completed because a rule in the domain was
 * violated, for example a duplicate slug. Translated to HTTP 400.
 */
public class InvalidRequestException extends RuntimeException {

	public InvalidRequestException(String message) {
		super(message);
	}
}