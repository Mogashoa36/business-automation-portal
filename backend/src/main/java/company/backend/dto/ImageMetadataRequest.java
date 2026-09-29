package company.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Editable metadata for an uploaded image. */
public record ImageMetadataRequest(

		@NotBlank(message = "Alt text is required so the image is described for screen reader users")
		@Size(max = 300, message = "Alt text must be 300 characters or fewer")
		String altText,

		@Size(max = 300, message = "Caption must be 300 characters or fewer")
		String caption) {
}