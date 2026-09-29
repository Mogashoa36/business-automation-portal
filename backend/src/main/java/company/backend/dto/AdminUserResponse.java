package company.backend.dto;

import java.time.Instant;

import company.backend.domain.AdminRole;

/**
 * The authenticated administrator, as returned to the admin client.
 *
 * <p>Deliberately contains no password field of any kind. There is no getter
 * here that could leak a hash even by accident.
 */
public record AdminUserResponse(Long id, String username, String email, String displayName, AdminRole role,
		boolean enabled, boolean mustChangePassword, Instant createdAt, Instant lastLoginAt) {

	public static AdminUserResponse of(company.backend.entity.AdminUser user) {
		return new AdminUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getDisplayName(),
				user.getRole(), user.isEnabled(), user.isMustChangePassword(), user.getCreatedAt(),
				user.getLastLoginAt());
	}
}