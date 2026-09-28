package company.backend.exception;

/**
 * Raised when a single client submits more contact messages than allowed
 * inside the configured window. Translated to HTTP 429.
 */
public class ContactSubmissionRateLimitException extends RuntimeException {

	private final long retryAfterSeconds;

	public ContactSubmissionRateLimitException(long retryAfterSeconds) {
		super("Too many enquiries were submitted from this connection. Please try again later.");
		this.retryAfterSeconds = retryAfterSeconds;
	}

	public long getRetryAfterSeconds() {
		return this.retryAfterSeconds;
	}
}
