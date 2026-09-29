package company.backend.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import company.backend.service.ImageValidator;

/**
 * Image upload settings for the administration portal.
 *
 * <p>Everything is configuration driven, and the storage directory is outside
 * the application's own resources so an uploaded file can never be served by the
 * application server's default static handler or reached through a path
 * traversal.
 *
 * @param directory absolute or relative path where uploads are written
 * @param maxFileBytes largest upload accepted, in bytes
 * @param allowedMimeTypes MIME types that may be uploaded
 * @param publicBasePath URL prefix the files are served under
 */
@ConfigurationProperties(prefix = "app.images")
public record ImageStorageProperties(String directory, long maxFileBytes, List<String> allowedMimeTypes,
		String publicBasePath, Duration sessionMinutes) {

	/** Used when no limit is configured, so a missing property cannot reject everything. */
	public static final long DEFAULT_MAX_BYTES = 5L * 1024 * 1024;

	/** Used when no directory is configured. */
	public static final String DEFAULT_DIRECTORY = "./uploads";

	public ImageStorageProperties {
		// Every value falls back to a working default rather than a zero or null.
		// A missing property must not silently disable uploads entirely, which is
		// both confusing and easy to miss.
		directory = (directory == null || directory.isBlank()) ? DEFAULT_DIRECTORY : directory;
		maxFileBytes = maxFileBytes <= 0 ? DEFAULT_MAX_BYTES : maxFileBytes;
		allowedMimeTypes = (allowedMimeTypes == null || allowedMimeTypes.isEmpty())
				? ImageValidator.ALLOWED_MIME_TYPES : List.copyOf(allowedMimeTypes);
		publicBasePath = (publicBasePath == null || publicBasePath.isBlank()) ? "/api/images/" : publicBasePath;
		sessionMinutes = sessionMinutes == null || sessionMinutes.isNegative() ? Duration.ofMinutes(15) : sessionMinutes;
	}

	/** The configured limit, expressed in whole megabytes, for error messages. */
	public long maxFileMegabytes() {
		return maxFileBytes / (1024 * 1024);
	}
}