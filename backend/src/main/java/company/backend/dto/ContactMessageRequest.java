package company.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming contact form payload.
 *
 * <p>The record is deliberately decoupled from the {@code ContactMessage}
 * entity so the public API contract can evolve without exposing persistence
 * details. Length limits are enforced here to protect the database and to keep
 * abusive payloads out of the service layer.
 */
public record ContactMessageRequest(

		@NotBlank(message = "Full name is required")
		@Size(max = 120, message = "Full name must be 120 characters or fewer")
		String fullName,

		@Size(max = 120, message = "Company must be 120 characters or fewer")
		String company,

		@NotBlank(message = "Email address is required")
		@Email(message = "Enter a valid email address")
		@Size(max = 180, message = "Email address must be 180 characters or fewer")
		String email,

		@Size(max = 40, message = "Phone number must be 40 characters or fewer")
		String phone,

		@NotBlank(message = "Please choose a service")
		@Size(max = 60, message = "Service must be 60 characters or fewer")
		String service,

		@NotBlank(message = "Message is required")
		@Size(min = 10, max = 4000, message = "Message must be between 10 and 4000 characters")
		String message) {
}
