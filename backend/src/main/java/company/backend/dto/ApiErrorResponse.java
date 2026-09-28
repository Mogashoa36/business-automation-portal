package company.backend.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Consistent error payload returned for every failed API call.
 *
 * @param timestamp when the error occurred
 * @param status the HTTP status code
 * @param error the HTTP status reason
 * @param message a human readable, safe to display message
 * @param fieldErrors validation problems keyed by field name (may be empty)
 * @param path the request path that failed
 */
public record ApiErrorResponse(Instant timestamp, int status, String error, String message,
		Map<String, String> fieldErrors, String path) {

	public static ApiErrorResponse of(int status, String error, String message, Map<String, String> fieldErrors,
			String path) {
		return new ApiErrorResponse(Instant.now(), status, error, message, fieldErrors == null ? Map.of() : fieldErrors,
				path);
	}
}
