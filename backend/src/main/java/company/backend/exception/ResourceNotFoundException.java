package company.backend.exception;

/** Raised when a requested resource does not exist. Translated to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
