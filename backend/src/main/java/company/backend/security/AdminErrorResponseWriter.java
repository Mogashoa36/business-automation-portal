package company.backend.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import company.backend.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Writes the API's standard JSON error body.
 *
 * <p>Shared by the authentication and authorization handlers so every failure
 * on a protected endpoint has the identical shape, and so none of them can
 * accidentally include a stack trace, a framework class name or a credential.
 */
final class AdminErrorResponseWriter {

	private AdminErrorResponseWriter() {
		// Utility class.
	}

	static void write(HttpServletResponse response, HttpStatus status, String error, String message, String path)
			throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		ApiErrorResponse body = new ApiErrorResponse(Instant.now(), status.value(), error, message, Map.of(), path);
		response.getWriter().write(toJson(body));
	}

	/**
	 * Minimal hand written serialiser. The error shape is fixed and small, and
	 * the response is often already committed by the time an entry point runs,
	 * so relying on message conversion here would be fragile.
	 */
	private static String toJson(ApiErrorResponse body) {
		StringBuilder json = new StringBuilder(160);
		json.append("{\"timestamp\":\"").append(body.timestamp()).append("\",");
		json.append("\"status\":").append(body.status()).append(',');
		json.append("\"error\":\"").append(escape(body.error())).append("\",");
		json.append("\"message\":\"").append(escape(body.message())).append("\",");
		json.append("\"fieldErrors\":{},");
		json.append("\"path\":\"").append(escape(body.path())).append("\"}");
		return json.toString();
	}

	private static String escape(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}