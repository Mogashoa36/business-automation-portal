package company.backend.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.AdminUserCreateRequest;
import company.backend.dto.AdminUserResponse;
import company.backend.dto.AdminUserUpdateRequest;
import company.backend.dto.ResetPasswordRequest;
import company.backend.service.ActivityLogService;
import company.backend.service.AdminUserService;
import jakarta.validation.Valid;

/**
 * Administrator account management.
 *
 * <p>Restricted to the ADMIN role by the security filter chain on
 * {@code /api/admin/users/**}. The role check happens on the server before
 * this controller runs, so hiding these links in the UI is presentation only.
 */
@RestController
@RequestMapping(path = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserController {

	private final AdminUserService adminUserService;
	private final ActivityLogService activityLogService;

	public AdminUserController(AdminUserService adminUserService, ActivityLogService activityLogService) {
		this.adminUserService = adminUserService;
		this.activityLogService = activityLogService;
	}

	@GetMapping
	public List<AdminUserResponse> findAll() {
		return this.adminUserService.findAll();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdminUserResponse> create(Authentication authentication,
			@Valid @RequestBody AdminUserCreateRequest request) {
		AdminUserResponse created = this.adminUserService.create(authentication.getName(), request);
		// The username is recorded, never the temporary password.
		this.activityLogService.record(authentication.getName(), "ADMIN_USER_CREATED", "AdminUser", "SUCCESS",
				created.username());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public AdminUserResponse update(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody AdminUserUpdateRequest request) {
		AdminUserResponse updated = this.adminUserService.update(authentication.getName(), id, request);
		this.activityLogService.record(authentication.getName(), "ADMIN_USER_UPDATED", "AdminUser", "SUCCESS",
				updated.username());
		return updated;
	}

	/**
	 * Sets a new password. The previous one is never revealed, because only a
	 * hash is ever stored, and the new secret is forced to change at next sign
	 * in.
	 */
	@PutMapping(path = "/{id}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
	public AdminUserResponse resetPassword(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody ResetPasswordRequest request) {
		AdminUserResponse updated = this.adminUserService.resetPassword(authentication.getName(), id,
				request.newPassword());
		// Only the fact of the reset is logged, never the new secret.
		this.activityLogService.record(authentication.getName(), "ADMIN_PASSWORD_RESET", "AdminUser", "SUCCESS",
				updated.username());
		return updated;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
		this.adminUserService.delete(authentication.getName(), id);
		this.activityLogService.record(authentication.getName(), "ADMIN_USER_DELETED", "AdminUser", "SUCCESS",
				"id " + id);
		return ResponseEntity.noContent().build();
	}
}