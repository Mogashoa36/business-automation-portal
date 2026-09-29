package company.backend.controller.admin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.SiteContentResponse;
import company.backend.service.ActivityLogService;
import company.backend.service.SiteContentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

/**
 * Editable site settings and SEO metadata.
 *
 * <p>ADMIN only. The service additionally restricts writes to an allow list of
 * keys, which is what guarantees application configuration (database
 * credentials, signing keys, environment variables) can never be reached
 * through the admin UI.
 */
@RestController
@RequestMapping(path = "/api/admin/site-content", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminSiteContentController {

	private final SiteContentService siteContentService;
	private final ActivityLogService activityLogService;

	public AdminSiteContentController(SiteContentService siteContentService, ActivityLogService activityLogService) {
		this.siteContentService = siteContentService;
		this.activityLogService = activityLogService;
	}

	@GetMapping
	public List<SiteContentResponse> findAll() {
		return this.siteContentService.findAll();
	}

	/**
	 * The key is matched with {@code :.+} because the allow listed keys
	 * themselves contain a colon, for example {@code seo:home:title}.
	 */
	@PutMapping(path = "/{key:.+}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public SiteContentResponse update(Authentication authentication, @PathVariable String key,
			@Valid @RequestBody UpdateRequest request) {
		SiteContentResponse updated = this.siteContentService.update(authentication.getName(), key, request.value());
		this.activityLogService.record(authentication.getName(), "SITE_CONTENT_UPDATED", "SiteContent", "SUCCESS", key);
		return updated;
	}

	/** Body of the site content update endpoint. */
	public record UpdateRequest(
			@Size(max = 2000, message = "Content must be 2000 characters or fewer") String value) {
	}
}