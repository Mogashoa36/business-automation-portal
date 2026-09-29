package company.backend.service;

import java.time.Instant;

/**
 * Sign in, sign out and password maintenance for administrators.
 */
public interface AuthService {

	/**
	 * Verifies credentials and establishes a server side session.
	 *
	 * @param identifier username or email address
	 * @param password the candidate secret, compared against a BCrypt hash
	 * @param clientId best effort caller identifier used for throttling
	 * @return the authenticated administrator
	 * @throws company.backend.exception.InvalidCredentialsException when the
	 * identifier or password is wrong, or the account is disabled
	 * @throws company.backend.exception.AccountLockedException when too many
	 * attempts have failed recently
	 */
	AdminSession authenticate(String identifier, String password, String clientId);

	/** Verifies the current password and replaces it with a new one. */
	void changePassword(Long adminId, String currentPassword, String newPassword);

	/** Reads the currently signed in administrator. */
	AdminSession current(Long adminId);
}