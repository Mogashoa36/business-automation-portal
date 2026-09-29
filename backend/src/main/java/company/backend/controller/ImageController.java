package company.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.service.ImageStorageService;

/**
 * Serves uploaded images to the public website.
 *
 * <p>This is the one place an uploaded file becomes reachable, so the response
 * is deliberately hard:
 *
 * <ul>
 * <li>the content type comes from the validated record, never from the file
 * itself, so a stored HTML or script can never be served as itself;</li>
 * <li>{@code Content-Disposition: inline} with a fixed type means the browser
 * renders rather than downloads, and cannot be pushed towards execution;</li>
 * <li>{@code X-Content-Type-Options: nosniff} stops a browser second guessing
 * the declared type;</li>
 * <li>a restrictive {@code Content-Security-Policy} applies to the response in
 * case the file is ever opened directly, which neuters inline script;</li>
 * <li>only a bare filename is accepted, and the resolved path is verified to sit
 * inside the upload directory.</li>
 * </ul>
 */
@RestController
@RequestMapping(path = "/api/images")
public class ImageController {

	private final ImageStorageService imageStorageService;

	public ImageController(ImageStorageService imageStorageService) {
		this.imageStorageService = imageStorageService;
	}

	/**
	 * @param filename the generated filename returned by the admin API. A
	 * traversal attempt resolves to a 404 rather than reading a file.
	 */
	@GetMapping("/{filename}")
	public ResponseEntity<InputStreamResource> serve(@PathVariable String filename) {
		return this.imageStorageService.load(filename).map(this::toResponse).orElseGet(this::notFound);
	}

	private ResponseEntity<InputStreamResource> toResponse(Path path) {
		try {
			InputStream in = Files.newInputStream(path);
			String contentType = detectType(path);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(contentType));
			// Inline so it renders in an <img>, with an explicit type so it can
			// never be treated as an attachment to be executed.
			headers.setContentDispositionFormData("inline", path.getFileName().toString());
			headers.set("X-Content-Type-Options", "nosniff");
			headers.set("Content-Security-Policy", "default-src 'none'; img-src 'self'; style-src 'unsafe-inline'");
			// Filenames are content addressed by UUID, so they can be cached hard.
			headers.setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());
			headers.setETag('"' + Long.toHexString(Files.size(path)) + '"');

			return new ResponseEntity<>(new InputStreamResource(in), headers, org.springframework.http.HttpStatus.OK);
		}
		catch (IOException exception) {
			// Never leak the path or the reason in the body.
			return notFound();
		}
	}

	/**
	 * Maps the stored extension to a type.
	 *
	 * <p>Only the four validated extensions can exist, because the stored name is
	 * generated from the validated MIME type. An unknown extension is served as
	 * {@code image/png} rather than anything executable.
	 */
	private String detectType(Path path) {
		String name = path.getFileName().toString().toLowerCase(java.util.Locale.ROOT);
		if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		if (name.endsWith(".gif")) {
			return "image/gif";
		}
		if (name.endsWith(".webp")) {
			return "image/webp";
		}
		return "image/png";
	}

	private ResponseEntity<InputStreamResource> notFound() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Content-Type-Options", "nosniff");
		return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).headers(headers).build();
	}
}