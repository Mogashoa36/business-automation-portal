package company.backend.controller.admin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import company.backend.dto.EnquiryResponse;
import company.backend.dto.EnquiryUpdateRequest;
import company.backend.dto.PageResponse;
import company.backend.service.ActivityLogService;
import company.backend.service.AdminEnquiryService;
import jakarta.validation.Valid;

/**
 * Contact enquiry triage.
 *
 * <p>Enquiries are private. This whole controller sits behind authentication
 * and no enquiry data is exposed anywhere in the public API.
 */
@RestController
@RequestMapping(path = "/api/admin/enquiries", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminEnquiryController {

	private final AdminEnquiryService adminEnquiryService;
	private final ActivityLogService activityLogService;

	public AdminEnquiryController(AdminEnquiryService adminEnquiryService, ActivityLogService activityLogService) {
		this.adminEnquiryService = adminEnquiryService;
		this.activityLogService = activityLogService;
	}

	@GetMapping
	public PageResponse<EnquiryResponse> findAll(@RequestParam(required = false) String status,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return this.adminEnquiryService.findAll(status, search, page, size);
	}

	@GetMapping("/{id}")
	public EnquiryResponse findById(@PathVariable Long id) {
		return this.adminEnquiryService.findById(id);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public EnquiryResponse update(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody EnquiryUpdateRequest request) {
		EnquiryResponse updated = this.adminEnquiryService.update(authentication.getName(), id, request);
		this.activityLogService.record(authentication.getName(), "ENQUIRY_UPDATED", "ContactMessage", "SUCCESS",
				updated.reference() + " -> " + updated.status());
		return updated;
	}
}