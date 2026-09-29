package company.backend.exception;

/**
 * Raised when sign in fails because the identifier or password is wrong.
 *
 * <p>Carries a neutral message so the API can never be used to discover whether
 * a given account exists.
 */
public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("The email, username or password is incorrect.");
	}
}