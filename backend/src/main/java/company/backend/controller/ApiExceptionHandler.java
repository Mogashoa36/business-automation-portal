package company.backend.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import company.backend.dto.ApiErrorResponse;
import company.backend.exception.AccountLockedException;
import company.backend.exception.ContactSubmissionRateLimitException;
import company.backend.exception.InvalidCredentialsException;
import company.backend.exception.InvalidRequestException;
import company.backend.exception.InvalidServiceInterestException;
import company.backend.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Translates exceptions into a single, predictable JSON error shape.
 *
 * <p>Internal details (stack traces, SQL, entity internals) are never returned
 * to callers; unexpected failures are logged server side and answered with a
 * neutral message.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request",
				"Please correct the highlighted fields and try again.", fieldErrors, request.getRequestURI());
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request",
				"The request body could not be read. Check the format and try again.", Map.of(),
				request.getRequestURI());
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(InvalidServiceInterestException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidService(InvalidServiceInterestException exception,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", exception.getMessage(),
				Map.of("service", exception.getMessage()), request.getRequestURI());
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", exception.getMessage(),
				Map.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(ContactSubmissionRateLimitException.class)
	public ResponseEntity<ApiErrorResponse> handleRateLimit(ContactSubmissionRateLimitException exception,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests",
				exception.getMessage(), Map.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.header(HttpHeaders.RETRY_AFTER, Long.toString(exception.getRetryAfterSeconds())).body(body);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidRequest(InvalidRequestException exception,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", exception.getMessage(),
				Map.of(), request.getRequestURI());
		return ResponseEntity.badRequest().body(body);
	}

	/**
	 * Wrong identifier or password. The message is deliberately identical for
	 * an unknown account, a wrong password and a disabled account, so the
	 * endpoint cannot be used to discover which accounts exist.
	 */
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
				exception.getMessage(), Map.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}

	/** Account or client is temporarily throttled. */
	@ExceptionHandler(AccountLockedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccountLocked(AccountLockedException exception,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests",
				exception.getMessage(), Map.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
			.header(HttpHeaders.RETRY_AFTER, Long.toString(exception.getRetryAfterSeconds()))
			.body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
		log.error("Unhandled exception while processing {}", request.getRequestURI(), exception);
		ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
				"Something went wrong on our side. Please try again later.", Map.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}
}
