package company.backend.service;

import company.backend.dto.ContactMessageRequest;
import company.backend.dto.ContactMessageResponse;

/**
 * Business operations for website contact enquiries.
 */
public interface ContactMessageService {

	/**
	 * Validates, normalises and stores a contact enquiry.
	 * @param request the validated payload received from the website
	 * @param clientId a best effort identifier of the caller, used for rate limiting
	 * @return the receipt shown to the sender
	 * @throws company.backend.exception.InvalidServiceInterestException if the requested
	 * service is not offered
	 * @throws company.backend.exception.ContactSubmissionRateLimitException if the
	 * caller exceeded the configured submission limit
	 */
	ContactMessageResponse submit(ContactMessageRequest request, String clientId);
}
