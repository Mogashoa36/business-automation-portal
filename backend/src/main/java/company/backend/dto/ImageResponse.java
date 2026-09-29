package company.backend.dto;

import java.time.Instant;

/** An uploaded image, as returned to the admin portal. */
public record ImageResponse(Long id, String url, String originalName, String mimeType, long sizeBytes, int width,
		int height, String altText, String caption, String uploadedBy, Instant createdAt) {
}