package company.backend.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.config.AdminSecurityProperties;
import company.backend.dto.AdminUserResponse;
import company.backend.entity.AdminUser;
import company.backend.exception.AccountLockedException;
import company.backend.exception.InvalidCredentialsException;
import company.backend.exception.InvalidRequestException;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.AdminUserRepository;
import company.backend.security.AdminUserPrincipal;
import company.backend.security.PasswordPolicy;

/**
 * Administrator authentication.
 *
 * <p>Credential verification is delegated to Spring Security's
 * {@code DaoAuthenticationProvider}, which compares a BCrypt hash. The
 * plaintext password exists only as a method argument for the duration of the
 * call: it is never stored, never written to the activity log and never
 * included in a response.
 */
@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final AdminUserRepository adminUserRepository;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final LoginThrottleService loginThrottleService;
	private final AdminSecurityProperties properties;

	public AuthServiceImpl(AdminUserRepository adminUserRepository, AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder, LoginThrottleService loginThrottleService,
			AdminSecurityProperties properties) {
		this.adminUserRepository = adminUserRepository;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.loginThrottleService = loginThrottleService;
		this.properties = properties;
	}

	@Override
	@Transactional
	public AdminSession authenticate(String identifier, String password, String clientId) {
		long retryAfter = this.loginThrottleService.retryAfterSeconds(clientId);
		if (retryAfter > 0) {
			throw new AccountLockedException("Too many sign in attempts. Please wait and try again.", retryAfter);
		}

		Instant now = Instant.now();
		AdminUser user = findByIdentifier(identifier);

		// Refuse a locked account before the password is ever compared.
		if (user != null && user.isLockedAt(now)) {
			long seconds = Math.max(1L, Duration.between(now, user.getLockedUntil()).toSeconds());
			throw new AccountLockedException("This account is temporarily locked. Please try again later.", seconds);
		}

		try {
			Authentication authentication = this.authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(identifier.trim(), password));
			AdminUserPrincipal principal = (AdminUserPrincipal) authentication.getPrincipal();
			AdminUser authenticated = requireById(principal.getId());
			authenticated.recordSuccessfulLogin(now);
			this.adminUserRepository.save(authenticated);
			this.loginThrottleService.recordSuccess(clientId);

			log.info("Administrator {} signed in", authenticated.getUsername());
			return new AdminSession(AdminUserResponse.of(authenticated), now, authenticated.isMustChangePassword());
		}
		catch (BadCredentialsException | DisabledException exception) {
			registerFailure(user, clientId);
			// One message for a wrong password, an unknown identifier and a
			// disabled account, so the endpoint cannot enumerate accounts.
			throw new InvalidCredentialsException();
		}
		catch (AuthenticationException exception) {
			registerFailure(user, clientId);
			throw new InvalidCredentialsException();
		}
	}

	@Override
	@Transactional
	public void changePassword(Long adminId, String currentPassword, String newPassword) {
		AdminUser user = requireById(adminId);

		// Re-verify the current password so a stolen session alone cannot lock
		// the real owner out of their own account.
		if (!this.passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
			log.warn("Rejected a password change for administrator {} because the current password was wrong",
					user.getUsername());
			throw new InvalidRequestException("Your current password is incorrect.");
		}
		if (currentPassword.equals(newPassword)) {
			throw new InvalidRequestException("Your new password must be different from the current one.");
		}

		String policyError = PasswordPolicy.validate(newPassword);
		if (policyError != null) {
			throw new InvalidRequestException(policyError);
		}

		user.setPasswordHash(this.passwordEncoder.encode(newPassword));
		user.resetFailedLogins();
		this.adminUserRepository.save(user);
		// The new password itself is never logged.
		log.info("Administrator {} changed their password", user.getUsername());
	}

	@Override
	@Transactional(readOnly = true)
	public AdminSession current(Long adminId) {
		return new AdminSession(AdminUserResponse.of(requireById(adminId)));
	}

	/**
	 * Records a failed attempt against the account and locks it once the limit
	 * is reached. The username is logged, never the submitted password.
	 */
	private void registerFailure(AdminUser user, String clientId) {
		this.loginThrottleService.recordFailure(clientId);

		if (user == null) {
			log.warn("Rejected an admin sign in for an unknown identifier");
			return;
		}

		AdminUser managed = this.adminUserRepository.findById(user.getId()).orElse(null);
		if (managed == null) {
			return;
		}
		managed.recordFailedLogin();
		if (managed.getFailedLoginAttempts() >= this.properties.maxFailedAttempts()) {
			managed.lockUntil(Instant.now().plus(Duration.ofMinutes(this.properties.lockoutMinutes())));
			log.warn("Locked administrator {} after {} consecutive failed sign in attempts", managed.getUsername(),
					managed.getFailedLoginAttempts());
		}
		this.adminUserRepository.save(managed);
	}

	private AdminUser findByIdentifier(String identifier) {
		if (identifier == null || identifier.isBlank()) {
			return null;
		}
		String candidate = identifier.trim().toLowerCase(Locale.ROOT);
		return this.adminUserRepository.findByUsername(candidate)
			.or(() -> this.adminUserRepository.findByEmail(candidate))
			.orElse(null);
	}

	private AdminUser requireById(Long adminId) {
		return this.adminUserRepository.findById(adminId)
			.orElseThrow(() -> new ResourceNotFoundException("No administrator found for this session."));
	}
}