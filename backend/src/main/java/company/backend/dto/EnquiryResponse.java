package company.backend.dto;

import java.time.Instant;

import company.backend.domain.EnquiryStatus;
import company.backend.entity.ContactMessage;

/**
 * A website enquiry as seen by an administrator.
 *
 * <p>Only ever returned from an authenticated admin endpoint. The public
 * contact API returns a receipt containing nothing but a reference.
 */
public record EnquiryResponse(Long id, String reference, String fullName, String company, String email, String phone,
		String service, String message, EnquiryStatus status, String internalNotes, Instant createdAt,
		Instant updatedAt) {

	public static EnquiryResponse from(ContactMessage message) {
		return new EnquiryResponse(message.getId(), message.getReference(), message.getFullName(),
				message.getCompany(), message.getEmail(), message.getPhone(), message.getService().getDisplayName(),
				message.getMessage(), message.getStatus(), message.getInternalNotes(), message.getCreatedAt(),
				message.getUpdatedAt());
	}
}