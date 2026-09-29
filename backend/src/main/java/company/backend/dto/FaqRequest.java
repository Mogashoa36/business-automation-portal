package company.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Create or update payload for a FAQ entry from the admin portal.
 */
public record FaqRequest(

		@NotBlank(message = "Question is required")
		@Size(max = 300, message = "Question must be 300 characters or fewer")
		String question,

		@NotBlank(message = "Answer is required")
		@Size(max = 4000, message = "Answer must be 4000 characters or fewer")
		String answer,

		@Size(max = 80, message = "Category must be 80 characters or fewer")
		String category,

		boolean published,

		@Size(min = 0, max = 10000, message = "Display order must be between 0 and 10000")
		Integer displayOrder) {
}