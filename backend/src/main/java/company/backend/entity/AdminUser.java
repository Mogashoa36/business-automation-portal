package company.backend.entity;

import java.time.Instant;
import java.util.Locale;

import company.backend.domain.AdminRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * An administrator who may sign in to the administration portal.
 *
 * <p>Only a BCrypt hash of the password is ever stored. The plaintext password
 * is never persisted, never returned by the API and never logged.
 *
 * <p>{@code mustChangePassword} forces a rotation of a bootstrap or reset
 * password at the next sign in, so a first use credential cannot quietly become
 * a long lived one.
 */
@Entity
@Table(name = "admin_users", indexes = {
		@Index(name = "idx_admin_users_username", columnList = "username", unique = true),
		@Index(name = "idx_admin_users_email", columnList = "email", unique = true) })
public class AdminUser {

	private static final int MIN_USERNAME_LENGTH = 3;
	private static final int MAX_USERNAME_LENGTH = 60;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Stored lower case so sign in is case insensitive. */
	@Column(name = "username", nullable = false, unique = true, length = 60)
	private String username;

	@Column(name = "email", nullable = false, unique = true, length = 180)
	private String email;

	@Column(name = "display_name", nullable = false, length = 120)
	private String displayName;

	/** BCrypt hash. Never serialised by the API. */
	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private AdminRole role;

	/** A disabled account cannot authenticate even with the right password. */
	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	/** Forces a password change before the account can be used for anything else. */
	@Column(name = "must_change_password", nullable = false)
	private boolean mustChangePassword;

	/** Consecutive failed sign ins. Reset on success and on lockout expiry. */
	@Column(name = "failed_login_attempts", nullable = false)
	private int failedLoginAttempts;

	/** While set and in the future, sign in is refused for this account. */
	@Column(name = "locked_until")
	private Instant lockedUntil;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "last_login_at")
	private Instant lastLoginAt;

	protected AdminUser() {
		// Required by JPA.
	}

	public AdminUser(String username, String email, String displayName, String passwordHash, AdminRole role,
			boolean mustChangePassword, Instant now) {
		this.username = normaliseUsername(username);
		this.email = normaliseEmail(email);
		this.displayName = displayName;
		this.passwordHash = passwordHash;
		this.role = role;
		this.enabled = true;
		this.mustChangePassword = mustChangePassword;
		this.failedLoginAttempts = 0;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public Long getId() {
		return this.id;
	}

	public String getUsername() {
		return this.username;
	}

	public String getEmail() {
		return this.email;
	}

	/** Email changes only through here, so it is always normalised. */
	public void updateEmail(String email) {
		this.email = normaliseEmail(email);
		this.updatedAt = Instant.now();
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	/** Setting a new hash always clears the forced change flag. */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
		this.mustChangePassword = false;
		this.updatedAt = Instant.now();
	}

	/**
	 * Used when an administrator resets somebody else's password. The new
	 * secret is only known to its owner, so it must be rotated at next sign in
	 * even though a hash was just supplied.
	 */
	public void forcePasswordChangeOnNextLogin() {
		this.mustChangePassword = true;
		this.updatedAt = Instant.now();
	}

	public AdminRole getRole() {
		return this.role;
	}

	public void setRole(AdminRole role) {
		this.role = role;
		this.updatedAt = Instant.now();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.updatedAt = Instant.now();
	}

	public boolean isMustChangePassword() {
		return this.mustChangePassword;
	}

	public int getFailedLoginAttempts() {
		return this.failedLoginAttempts;
	}

	public void recordFailedLogin() {
		this.failedLoginAttempts++;
	}

	public void resetFailedLogins() {
		this.failedLoginAttempts = 0;
		this.lockedUntil = null;
	}

	public Instant getLockedUntil() {
		return this.lockedUntil;
	}

	/** @return true when the lockout window is still open at the given instant. */
	public boolean isLockedAt(Instant now) {
		return this.lockedUntil != null && this.lockedUntil.isAfter(now);
	}

	public void lockUntil(Instant instant) {
		this.lockedUntil = instant;
	}

	public Instant getCreatedAt() {
		return this.createdAt;
	}

	public Instant getUpdatedAt() {
		return this.updatedAt;
	}

	public Instant getLastLoginAt() {
		return this.lastLoginAt;
	}

	public void recordSuccessfulLogin(Instant now) {
		this.lastLoginAt = now;
		this.failedLoginAttempts = 0;
		this.lockedUntil = null;
		this.updatedAt = now;
	}

	/** Sign in is matched case insensitively against either identifier. */
	public boolean matchesIdentifier(String candidate) {
		if (candidate == null) {
			return false;
		}
		String normalised = candidate.trim().toLowerCase(Locale.ROOT);
		return this.username.equals(normalised) || this.email.equals(normalised);
	}

	/** Usernames stay inside a sane length and character range. */
	public static boolean isValidUsername(String username) {
		if (username == null) {
			return false;
		}
		String candidate = username.trim();
		if (candidate.length() < MIN_USERNAME_LENGTH || candidate.length() > MAX_USERNAME_LENGTH) {
			return false;
		}
		return candidate.chars().allMatch(character -> Character.isLetterOrDigit(character)
				|| character == '.' || character == '_' || character == '-');
	}

	public static String normaliseUsername(String username) {
		return username == null ? null : username.trim().toLowerCase(Locale.ROOT);
	}

	public static String normaliseEmail(String email) {
		return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
	}
}
