package company.backend.security;

/**
 * Server side password policy.
 *
 * <p>The browser also validates these rules for immediate feedback, but this
 * class is the actual control: a client that skips its own checks gains
 * nothing. The policy is deliberately length first, because length matters far
 * more than character class rules.
 */
public final class PasswordPolicy {

	public static final int MIN_LENGTH = 12;
	public static final int MAX_LENGTH = 100;

	private PasswordPolicy() {
		// Utility class.
	}

	/**
	 * @return {@code null} when the password is acceptable, otherwise a short
	 * explanation safe to show the administrator
	 */
	public static String validate(String password) {
		if (password == null || password.isEmpty()) {
			return "Password is required.";
		}
		if (password.length() < MIN_LENGTH) {
			return "Password must be at least " + MIN_LENGTH + " characters long.";
		}
		if (password.length() > MAX_LENGTH) {
			// BCrypt only considers the first 72 bytes, so a hard cap keeps the
			// stored hash and the supplied value describing the same secret.
			return "Password must be " + MAX_LENGTH + " characters or fewer.";
		}
		if (password.chars().noneMatch(Character::isUpperCase)) {
			return "Password must contain at least one uppercase letter.";
		}
		if (password.chars().noneMatch(Character::isLowerCase)) {
			return "Password must contain at least one lowercase letter.";
		}
		if (password.chars().noneMatch(Character::isDigit)) {
			return "Password must contain at least one number.";
		}
		return null;
	}

	public static boolean isValid(String password) {
		return validate(password) == null;
	}
}