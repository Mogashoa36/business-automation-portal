package company.backend.service;

import company.backend.dto.EnquiryResponse;
import company.backend.dto.EnquiryUpdateRequest;
import company.backend.dto.PageResponse;

/** Reads and triages the private website enquiries. */
public interface AdminEnquiryService {

	/**
	 * @param status optional status filter
	 * @param search optional free text filter across name, company, email and
	 * reference
	 * @param page zero based page index
	 * @param size page size, bounded server side
	 */
	PageResponse<EnquiryResponse> findAll(String status, String search, int page, int size);

	EnquiryResponse findById(Long id);

	EnquiryResponse update(String actor, Long id, EnquiryUpdateRequest request);
}