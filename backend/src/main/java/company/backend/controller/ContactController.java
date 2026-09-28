package company.backend.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.ContactMessageRequest;
import company.backend.dto.ContactMessageResponse;
import company.backend.service.ContactMessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Public endpoint backing the website contact form.
 *
 * <p>The controller only adapts the HTTP request; validation, abuse protection
 * and persistence are handled by {@link ContactMessageService}.
 */
@RestController
@RequestMapping(path = "/api/contact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactController {

	private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

	private final ContactMessageService contactMessageService;

	public ContactController(ContactMessageService contactMessageService) {
		this.contactMessageService = contactMessageService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContactMessageResponse> submit(@Valid @RequestBody ContactMessageRequest request,
			@RequestHeader(value = FORWARDED_FOR_HEADER, required = false) String forwardedFor,
			HttpServletRequest httpRequest) {
		ContactMessageResponse receipt = this.contactMessageService.submit(request,
				resolveClientId(forwardedFor, httpRequest));
		return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
	}

	/**
	 * Best effort client identifier used for rate limiting. The forwarded header
	 * is only trusted when the application runs behind a reverse proxy that sets
	 * it, which is the deployment this API is designed for.
	 */
	private String resolveClientId(String forwardedFor, HttpServletRequest httpRequest) {
		return Optional.ofNullable(forwardedFor)
				.map(value -> value.split(",")[0].trim())
				.filter(value -> !value.isEmpty())
				.orElseGet(httpRequest::getRemoteAddr);
	}
}
