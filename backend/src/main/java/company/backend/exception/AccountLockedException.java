package company.backend.exception;

/**
 * Raised when an account has been locked by too many failed sign in attempts.
 * Translated to HTTP 429 with a {@code Retry-After} header.
 */
public class AccountLockedException extends RuntimeException {

	private final long retryAfterSeconds;

	public AccountLockedException(String message, long retryAfterSeconds) {
		super(message);
		this.retryAfterSeconds = retryAfterSeconds;
	}

	public long getRetryAfterSeconds() {
		return this.retryAfterSeconds;
	}
}