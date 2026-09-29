package company.backend.service;

import java.time.Instant;
import java.util.Locale;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.domain.EnquiryStatus;
import company.backend.dto.EnquiryResponse;
import company.backend.dto.EnquiryUpdateRequest;
import company.backend.dto.PageResponse;
import company.backend.entity.ContactMessage;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.ContactMessageRepository;

/**
 * Enquiry triage for administrators.
 *
 * <p>Search and filtering happen in the database with a bounded page size, so
 * the admin table stays responsive and the API never returns an unbounded
 * result set.
 */
@Service
public class AdminEnquiryServiceImpl implements AdminEnquiryService {

	private static final int MAX_PAGE_SIZE = 100;
	private static final int DEFAULT_PAGE_SIZE = 20;

	private final ContactMessageRepository contactMessageRepository;

	public AdminEnquiryServiceImpl(ContactMessageRepository contactMessageRepository) {
		this.contactMessageRepository = contactMessageRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<EnquiryResponse> findAll(String status, String search, int page, int size) {
		EnquiryStatus statusFilter = parseStatus(status);
		// Escaped so a user supplied term cannot act as a wildcard.
		String searchFilter = normaliseSearch(search);

		Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, MAX_PAGE_SIZE)));
		return PageResponse.of(this.contactMessageRepository.search(statusFilter, searchFilter, pageable)
			.map(EnquiryResponse::from));
	}

	@Override
	@Transactional(readOnly = true)
	public EnquiryResponse findById(Long id) {
		return EnquiryResponse.from(require(id));
	}

	@Override
	@Transactional
	public EnquiryResponse update(String actor, Long id, EnquiryUpdateRequest request) {
		ContactMessage message = require(id);

		String trimmedStatus = trimToNull(request.status());
		if (trimmedStatus != null) {
			EnquiryStatus resolved = EnquiryStatus.findByValue(trimmedStatus).orElse(null);
			if (resolved != null) {
				message.setStatus(resolved);
			}
		}

		if (request.internalNotes() != null) {
			message.setInternalNotes(trimToNull(request.internalNotes()));
		}
		message.touch(Instant.now());

		return EnquiryResponse.from(this.contactMessageRepository.save(message));
	}

	private ContactMessage require(Long id) {
		return this.contactMessageRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No enquiry found with id " + id + "."));
	}

	/** An unknown status is treated as "no filter" rather than an error. */
	private EnquiryStatus parseStatus(String status) {
		String trimmed = trimToNull(status);
		return trimmed == null ? null : EnquiryStatus.findByValue(trimmed).orElse(null);
	}

	private String normaliseSearch(String search) {
		String trimmed = trimToNull(search);
		if (trimmed == null) {
			return null;
		}
		// LIKE wildcards in user input are neutralised; only a literal match
		// on the sanitised term is intended.
		return "%" + trimmed.toLowerCase(Locale.ROOT).replace("%", "").replace("_", "") + "%";
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}