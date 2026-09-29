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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import company.backend.dto.ImageMetadataRequest;
import company.backend.dto.ImageResponse;
import company.backend.service.ActivityLogService;
import company.backend.service.ImageStorageService;
import jakarta.validation.Valid;

/**
 * Image management for the administration portal.
 *
 * <p>Reachable by an authenticated administrator, and reserved to the ADMIN
 * role by the filter chain on {@code /api/admin/images/**}.
 *
 * <p>No filesystem path is ever accepted, returned or exposed. The client sends
 * a file and metadata; the server decides where it goes and returns only a URL
 * under its own public path.
 */
@RestController
@RequestMapping(path = "/api/admin/images", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminImageController {

	private final ImageStorageService imageStorageService;
	private final ActivityLogService activityLogService;

	public AdminImageController(ImageStorageService imageStorageService, ActivityLogService activityLogService) {
		this.imageStorageService = imageStorageService;
		this.activityLogService = activityLogService;
	}

	@GetMapping
	public List<ImageResponse> list() {
		return this.imageStorageService.list();
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ImageResponse> upload(Authentication authentication,
			@RequestPart("file") MultipartFile file,
			@RequestParam(value = "altText", required = false) String altText,
			@RequestParam(value = "caption", required = false) String caption) {

		ImageResponse stored = this.imageStorageService.store(file, altText, caption, authentication.getName());
		// The original filename is recorded; never a path, and never content.
		this.activityLogService.record(authentication.getName(), "IMAGE_UPLOADED", "ContentImage", "SUCCESS",
				stored.originalName());
		return ResponseEntity.status(HttpStatus.CREATED).body(stored);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ImageResponse updateMetadata(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody ImageMetadataRequest request) {
		ImageResponse updated = this.imageStorageService.updateMetadata(id, request);
		this.activityLogService.record(authentication.getName(), "IMAGE_UPDATED", "ContentImage", "SUCCESS", null);
		return updated;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
		this.imageStorageService.delete(authentication.getName(), id);
		this.activityLogService.record(authentication.getName(), "IMAGE_DELETED", "ContentImage", "SUCCESS", "id " + id);
		return ResponseEntity.noContent().build();
	}
}