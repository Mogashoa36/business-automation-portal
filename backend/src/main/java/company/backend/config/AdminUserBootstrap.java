package company.backend.config;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import company.backend.domain.AdminRole;
import company.backend.entity.AdminUser;
import company.backend.repo.AdminUserRepository;
import company.backend.security.PasswordPolicy;

/**
 * First run bootstrap for the administration portal.
 *
 * <p>There is no default account and no default password anywhere in this
 * codebase. The first administrator exists only if the operator supplies
 * bootstrap values through the environment:
 *
 * <pre>
 *   APP_ADMIN_BOOTSTRAP_USERNAME
 *   APP_ADMIN_BOOTSTRAP_EMAIL
 *   APP_ADMIN_BOOTSTRAP_PASSWORD
 *   APP_ADMIN_BOOTSTRAP_DISPLAY_NAME
 * </pre>
 *
 * <p>The account is created with {@code mustChangePassword} set, so the
 * bootstrap secret has to be rotated at the first sign in and cannot become a
 * long lived credential. The password is never logged, never echoed back to the
 * console and never written to the activity log; only the username is
 * mentioned, and only when an account is actually created.
 *
 * <p>When no bootstrap values are configured - the normal case once a real
 * administrator exists - the runner simply does nothing.
 */
@Component
public class AdminUserBootstrap implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AdminUserBootstrap.class);

	private final AdminUserRepository adminUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final AdminSecurityProperties properties;

	public AdminUserBootstrap(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder,
			AdminSecurityProperties properties) {
		this.adminUserRepository = adminUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.properties = properties;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (this.adminUserRepository.count() > 0) {
			return;
		}

		String username = trimToNull(this.properties.bootstrapUsername());
		String email = trimToNull(this.properties.bootstrapEmail());
		String password = this.properties.bootstrapPassword();

		if (username == null || email == null || password == null) {
			log.info("No administrator exists yet and no bootstrap credentials are configured. "
					+ "Set APP_ADMIN_BOOTSTRAP_USERNAME, APP_ADMIN_BOOTSTRAP_EMAIL and APP_ADMIN_BOOTSTRAP_PASSWORD "
					+ "to create the first administrator.");
			return;
		}

		String policyError = PasswordPolicy.validate(password);
		if (policyError != null) {
			// Refuse to create a weak account rather than silently accepting it.
			log.error("The configured bootstrap password was rejected: {}. No administrator was created.", policyError);
			return;
		}
		if (!AdminUser.isValidUsername(username)) {
			log.error("The configured bootstrap username is not a valid username. No administrator was created.");
			return;
		}

		String displayName = trimToNull(this.properties.bootstrapDisplayName());
		AdminUser administrator = new AdminUser(username, email, displayName == null ? username : displayName,
				this.passwordEncoder.encode(password), AdminRole.ADMIN, true, Instant.now());
		this.adminUserRepository.save(administrator);

		// Only the username. The password is never written to a log.
		log.info("Created the first administrator '{}'. The password must be changed at first sign in.", username);
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}