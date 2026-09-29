package company.backend.controller.admin;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.ChangePasswordRequest;
import company.backend.dto.LoginRequest;
import company.backend.dto.LoginResponse;
import company.backend.exception.InvalidRequestException;
import company.backend.security.AdminUserPrincipal;
import company.backend.service.ActivityLogService;
import company.backend.service.AdminSession;
import company.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Sign in, sign out and the current session, for the administration portal.
 *
 * <p>On success the security context is written to the HTTP session, so the
 * browser receives an opaque {@code HttpOnly} session cookie rather than a
 * token that script could read. Signing out invalidates the session server
 * side, so the cookie stops working immediately rather than merely being
 * cleared in the browser.
 *
 * <p>Authorization is enforced by the security filter chain, not here. This
 * controller only adapts HTTP to the authentication service.
 */
@RestController
@RequestMapping(path = "/api/admin/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminAuthController {

	private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

	private final AuthService authService;
	private final ActivityLogService activityLogService;
	private final SecurityContextRepository securityContextRepository;

	public AdminAuthController(AuthService authService, ActivityLogService activityLogService,
			SecurityContextRepository securityContextRepository) {
		this.authService = authService;
		this.activityLogService = activityLogService;
		this.securityContextRepository = securityContextRepository;
	}

	@PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public LoginResponse login(@Valid @RequestBody LoginRequest request,
			@RequestHeader(value = FORWARDED_FOR_HEADER, required = false) String forwardedFor,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		AdminSession session = this.authService.authenticate(request.identifier(), request.password(),
				resolveClientId(forwardedFor, httpRequest));

		/*
		 * The AuthenticationManager has already placed the context in the
		 * SecurityContextHolder. Persisting it here is what turns the
		 * authentication into a server side session, and the session id has
		 * already been rotated by the session fixation policy.
		 */
		this.securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);

		// The username is safe to log; the password is never referenced here.
		this.activityLogService.record(session.user().username(), "ADMIN_LOGIN", "AdminSession", "SUCCESS", null);
		return new LoginResponse(session);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof AdminUserPrincipal principal) {
			this.activityLogService.record(principal.getUsername(), "ADMIN_LOGOUT", "AdminSession", "SUCCESS", null);
		}

		// Invalidate server side so the cookie is useless immediately.
		HttpSession session = httpRequest.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
		return ResponseEntity.noContent().build();
	}

	/**
	 * The signed in administrator. Used by the Angular guard to restore session
	 * state after a page refresh, and to discover that a password change is due.
	 */
	@GetMapping("/me")
	public LoginResponse me(Authentication authentication) {
		return new LoginResponse(this.authService.current(adminId(authentication)));
	}

	@PostMapping(path = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
			Authentication authentication, HttpServletRequest httpRequest) {

		if (!request.newPassword().equals(request.confirmPassword())) {
			throw new InvalidRequestException("The new password and its confirmation do not match.");
		}

		this.authService.changePassword(adminId(authentication), request.currentPassword(), request.newPassword());
		this.activityLogService.record(authentication.getName(), "ADMIN_PASSWORD_CHANGED", "AdminUser", "SUCCESS", null);

		// End the current session so the old credential cannot keep it alive.
		HttpSession session = httpRequest.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
		return ResponseEntity.noContent().build();
	}

	private Long adminId(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof AdminUserPrincipal principal)) {
			throw new InvalidRequestException("No administrator session is active.");
		}
		return principal.getId();
	}

	/**
	 * Best effort caller identifier for throttling. The forwarded header is
	 * only trusted behind a reverse proxy, which is the intended deployment.
	 */
	private String resolveClientId(String forwardedFor, HttpServletRequest httpRequest) {
		return Optional.ofNullable(forwardedFor)
			.map(value -> value.split(",")[0].trim())
			.filter(value -> !value.isEmpty())
			.orElseGet(httpRequest::getRemoteAddr);
	}
}