package company.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import company.backend.domain.AdminRole;

/** Update an existing administrator. The password is changed separately. */
public record AdminUserUpdateRequest(

		@NotBlank(message = "Email address is required")
		@Email(message = "Enter a valid email address")
		@Size(max = 180, message = "Email address must be 180 characters or fewer")
		String email,

		@NotBlank(message = "Display name is required")
		@Size(max = 120, message = "Display name must be 120 characters or fewer")
		String displayName,

		@NotNull(message = "Choose a role")
		AdminRole role,

		@NotNull(message = "Choose whether the account is enabled")
		Boolean enabled) {
}