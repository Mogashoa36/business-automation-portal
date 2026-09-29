package company.backend.dto;

import java.time.Instant;

/** One editable content value. */
public record SiteContentResponse(String key, String value, String updatedBy, Instant updatedAt) {
}