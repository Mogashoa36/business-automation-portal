package company.backend.dto;

import jakarta.validation.constraints.Size;

/** Update an enquiry's workflow status and internal notes. */
public record EnquiryUpdateRequest(String status,

		@Size(max = 4000, message = "Internal notes must be 4000 characters or fewer")
		String internalNotes) {
}