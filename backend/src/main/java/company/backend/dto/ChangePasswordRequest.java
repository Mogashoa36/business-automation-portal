package company.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Change password request.
 *
 * <p>The current password is re-verified server side, and the new password is
 * checked against the password policy before it is hashed. Neither value is
 * ever logged or returned.
 */
public record ChangePasswordRequest(

		@NotBlank(message = "Enter your current password")
		@Size(max = 100, message = "Password must be 100 characters or fewer")
		String currentPassword,

		@NotBlank(message = "Enter a new password")
		@Size(max = 100, message = "Password must be 100 characters or fewer")
		String newPassword,

		@NotBlank(message = "Confirm your new password")
		@Size(max = 100, message = "Password must be 100 characters or fewer")
		String confirmPassword) {
}