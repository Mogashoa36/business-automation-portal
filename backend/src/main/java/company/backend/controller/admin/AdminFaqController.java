package company.backend.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.FaqRequest;
import company.backend.dto.FaqResponse;
import company.backend.service.ActivityLogService;
import company.backend.service.AdminFaqService;
import jakarta.validation.Valid;

/** FAQ management. Requires an authenticated ADMIN or EDITOR. */
@RestController
@RequestMapping(path = "/api/admin/faqs", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminFaqController {

	private final AdminFaqService adminFaqService;
	private final ActivityLogService activityLogService;

	public AdminFaqController(AdminFaqService adminFaqService, ActivityLogService activityLogService) {
		this.adminFaqService = adminFaqService;
		this.activityLogService = activityLogService;
	}

	@GetMapping
	public List<FaqResponse> findAll() {
		return this.adminFaqService.findAll();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FaqResponse> create(Authentication authentication, @Valid @RequestBody FaqRequest request) {
		FaqResponse created = this.adminFaqService.create(authentication.getName(), request);
		this.activityLogService.record(authentication.getName(), "FAQ_CREATED", "FaqItem", "SUCCESS",
				created.question());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public FaqResponse update(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody FaqRequest request) {
		FaqResponse updated = this.adminFaqService.update(authentication.getName(), id, request);
		this.activityLogService.record(authentication.getName(), "FAQ_UPDATED", "FaqItem", "SUCCESS",
				updated.question());
		return updated;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
		this.adminFaqService.delete(authentication.getName(), id);
		this.activityLogService.record(authentication.getName(), "FAQ_DELETED", "FaqItem", "SUCCESS", "id " + id);
		return ResponseEntity.noContent().build();
	}
}