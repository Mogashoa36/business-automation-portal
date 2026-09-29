package company.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import company.backend.domain.AdminRole;

/**
 * Create an administrator account.
 *
 * <p>{@code temporaryPassword} is a one-time secret the new administrator must
 * change at first sign in. It is only ever received in this request body, is
 * stored exclusively as a BCrypt hash, and is never echoed back by any
 * response.
 */
public record AdminUserCreateRequest(

		@NotBlank(message = "Username is required")
		@Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
		String username,

		@NotBlank(message = "Email address is required")
		@Email(message = "Enter a valid email address")
		@Size(max = 180, message = "Email address must be 180 characters or fewer")
		String email,

		@NotBlank(message = "Display name is required")
		@Size(max = 120, message = "Display name must be 120 characters or fewer")
		String displayName,

		@NotNull(message = "Choose a role")
		AdminRole role,

		@NotBlank(message = "A temporary password is required")
		@Size(max = 100, message = "Password must be 100 characters or fewer")
		String temporaryPassword) {
}