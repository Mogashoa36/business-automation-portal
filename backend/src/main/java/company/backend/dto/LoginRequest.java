package company.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Administrator sign in request.
 *
 * <p>The field is named {@code identifier} because either the username or the
 * email address is accepted. The value is only ever compared against a BCrypt
 * hash server side.
 */
public record LoginRequest(

		@NotBlank(message = "Enter your email or username")
		@Size(max = 180, message = "Identifier must be 180 characters or fewer")
		String identifier,

		@NotBlank(message = "Enter your password")
		@Size(max = 100, message = "Password must be 100 characters or fewer")
		String password) {
}