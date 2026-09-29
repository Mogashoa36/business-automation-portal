package company.backend.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Answers an authenticated request that the account's role does not permit
 * with JSON {@code 403}.
 *
 * <p>The client shows this as a permission message rather than signing the
 * user out, so a valid session is never discarded because of a single denied
 * action.
 */
@Component
public class AdminAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException {
		AdminErrorResponseWriter.write(response, HttpStatus.FORBIDDEN, "Forbidden",
				"Your account does not have permission to perform this action.", request.getRequestURI());
	}
}