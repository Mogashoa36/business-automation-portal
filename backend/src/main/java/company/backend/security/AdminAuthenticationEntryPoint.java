package company.backend.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Answers an unauthenticated request to a protected endpoint with JSON
 * {@code 401}.
 *
 * <p>The Angular admin client treats {@code 401} as "the session ended" and
 * returns to the login screen, which is why the status has to be a real 401 and
 * not a redirect to a login page.
 */
@Component
public class AdminAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		AdminErrorResponseWriter.write(response, HttpStatus.UNAUTHORIZED, "Unauthorized",
				"Your administrator session has ended. Please sign in again.", request.getRequestURI());
	}
}