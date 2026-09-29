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

import company.backend.dto.PortfolioProjectResponse;
import company.backend.service.ActivityLogService;
import company.backend.service.AdminPortfolioService;
import company.backend.service.PortfolioProjectCommand;
import jakarta.validation.Valid;

/**
 * Portfolio case study management.
 *
 * <p>Reachable only by an authenticated ADMIN or EDITOR; the filter chain
 * rejects anonymous callers before this controller runs. Every write is
 * recorded in the activity log.
 */
@RestController
@RequestMapping(path = "/api/admin/portfolio", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminPortfolioController {

	private final AdminPortfolioService adminPortfolioService;
	private final ActivityLogService activityLogService;

	public AdminPortfolioController(AdminPortfolioService adminPortfolioService,
			ActivityLogService activityLogService) {
		this.adminPortfolioService = adminPortfolioService;
		this.activityLogService = activityLogService;
	}

	@GetMapping
	public List<PortfolioProjectResponse> findAll() {
		return this.adminPortfolioService.findAll();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PortfolioProjectResponse> create(Authentication authentication,
			@Valid @RequestBody PortfolioProjectRequest request) {
		PortfolioProjectResponse created = this.adminPortfolioService.create(authentication.getName(),
				request.toCommand());
		this.activityLogService.record(authentication.getName(), "PORTFOLIO_CREATED", "PortfolioProject",
				"SUCCESS", created.slug());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public PortfolioProjectResponse update(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody PortfolioProjectRequest request) {
		PortfolioProjectResponse updated = this.adminPortfolioService.update(authentication.getName(), id,
				request.toCommand());
		this.activityLogService.record(authentication.getName(), "PORTFOLIO_UPDATED", "PortfolioProject", "SUCCESS",
				updated.slug());
		return updated;
	}

	@PutMapping(path = "/{id}/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public PortfolioProjectResponse publish(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody PublishRequest request) {
		PortfolioProjectResponse updated = this.adminPortfolioService.setPublished(authentication.getName(), id,
				request.published());
		this.activityLogService.record(authentication.getName(),
				request.published() ? "PORTFOLIO_PUBLISHED" : "PORTFOLIO_UNPUBLISHED", "PortfolioProject", "SUCCESS",
				updated.slug());
		return updated;
	}

	/** Saves a new display order. Unknown ids are ignored rather than failing the save. */
	@PutMapping(path = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> reorder(Authentication authentication, @Valid @RequestBody ReorderRequest request) {
		this.adminPortfolioService.reorder(authentication.getName(), request.ids());
		this.activityLogService.record(authentication.getName(), "PORTFOLIO_REORDERED", "PortfolioProject", "SUCCESS",
				request.ids().size() + " projects");
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
		this.adminPortfolioService.delete(authentication.getName(), id);
		this.activityLogService.record(authentication.getName(), "PORTFOLIO_DELETED", "PortfolioProject", "SUCCESS",
				"id " + id);
		return ResponseEntity.noContent().build();
	}
}