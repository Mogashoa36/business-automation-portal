package company.backend.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security settings for the administration portal.
 *
 * <p>Everything is read from configuration so no secret is ever committed to
 * source control. Administrator sessions are held server side, so there is no
 * signing key to manage: the browser only ever holds an opaque session cookie.
 *
 * @param sessionMinutes idle timeout of an administrator session
 * @param maxFailedAttempts consecutive failed sign ins before an account is
 * locked
 * @param lockoutMinutes how long a locked account stays locked
 * @param allowedOrigins origins permitted to call the admin API with
 * credentials
 * @param bootstrapUsername optional first administrator username, used only
 * when no administrator exists yet
 * @param bootstrapPassword optional first administrator password, read from the
 * environment and never logged
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminSecurityProperties(int sessionMinutes, int maxFailedAttempts, int lockoutMinutes,
		List<String> allowedOrigins, String bootstrapUsername, String bootstrapPassword, String bootstrapEmail,
		String bootstrapDisplayName) {

	public AdminSecurityProperties {
		sessionMinutes = sessionMinutes < 1 ? 30 : sessionMinutes;
		maxFailedAttempts = maxFailedAttempts < 1 ? 5 : maxFailedAttempts;
		lockoutMinutes = lockoutMinutes < 1 ? 15 : lockoutMinutes;
		allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
	}
}