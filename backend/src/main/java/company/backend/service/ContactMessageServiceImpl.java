package company.backend.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.config.ContactProperties;
import company.backend.domain.ServiceInterest;
import company.backend.dto.ContactMessageRequest;
import company.backend.dto.ContactMessageResponse;
import company.backend.entity.ContactMessage;
import company.backend.exception.ContactSubmissionRateLimitException;
import company.backend.exception.InvalidServiceInterestException;
import company.backend.repo.ContactMessageRepository;

/**
 * Stores website enquiries and keeps the public endpoint usable by applying a
 * simple per client submission limit.
 *
 * <p>All validation, normalisation and abuse handling lives here so the
 * controller stays a thin transport layer.
 */
@Service
public class ContactMessageServiceImpl implements ContactMessageService {

	private static final Logger log = LoggerFactory.getLogger(ContactMessageServiceImpl.class);

	private static final DateTimeFormatter REFERENCE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);
	private static final int REFERENCE_SUFFIX_LENGTH = 4;
	private static final int MAX_TRACKED_CLIENTS = 1000;

	private final ContactMessageRepository contactMessageRepository;
	private final ContactProperties contactProperties;
	private final SecureRandom secureRandom = new SecureRandom();

	/** Submission timestamps per client, used for the sliding window rate limit. */
	private final Map<String, Deque<Instant>> recentSubmissions = new ConcurrentHashMap<>();

	public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository,
			ContactProperties contactProperties) {
		this.contactMessageRepository = contactMessageRepository;
		this.contactProperties = contactProperties;
	}

	@Override
	@Transactional
	public ContactMessageResponse submit(ContactMessageRequest request, String clientId) {
		ServiceInterest service = resolveServiceInterest(request.service());
		String fullName = trimToNull(request.fullName());
		String email = trimToNull(request.email()).toLowerCase(Locale.ROOT);
		String message = trimToNull(request.message());

		enforceRateLimit(clientId);

		ContactMessage saved = this.contactMessageRepository.save(new ContactMessage(fullName, trimToNull(request.company()),
				email, trimToNull(request.phone()), service, message, nextReference(), Instant.now()));

		log.info("Stored contact enquiry {} for service {}", saved.getReference(), saved.getService());

		return new ContactMessageResponse(saved.getReference(), saved.getService().getDisplayName(), saved.getCreatedAt(),
				"Thank you. Your enquiry has been received and a member of our team will get back to you.");
	}

	private ServiceInterest resolveServiceInterest(String rawValue) {
		Optional<ServiceInterest> resolved = ServiceInterest.findByValue(rawValue);
		return resolved.orElseThrow(() -> new InvalidServiceInterestException(rawValue));
	}

	private String nextReference() {
		for (int attempt = 0; attempt < 5; attempt++) {
			String candidate = "CM-" + REFERENCE_DATE.format(Instant.now()) + "-"
					+ randomReferenceSuffix();
			if (!this.contactMessageRepository.existsByReference(candidate)) {
				return candidate;
			}
		}
		// Practically unreachable: 4 random base 36 characters.
		return "CM-" + REFERENCE_DATE.format(Instant.now()) + "-" + UUID.randomUUID().toString().substring(0, 8);
	}

	private String randomReferenceSuffix() {
		final String alphabet = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
		StringBuilder suffix = new StringBuilder(REFERENCE_SUFFIX_LENGTH);
		for (int i = 0; i < REFERENCE_SUFFIX_LENGTH; i++) {
			suffix.append(alphabet.charAt(this.secureRandom.nextInt(alphabet.length())));
		}
		return suffix.toString();
	}

	private void enforceRateLimit(String clientId) {
		Instant now = Instant.now();
		Instant windowStart = now.minus(this.contactProperties.rateWindow());

		if (this.recentSubmissions.size() > MAX_TRACKED_CLIENTS) {
			purgeExpiredClients(windowStart);
		}

		Deque<Instant> submissions = this.recentSubmissions.computeIfAbsent(key(clientId), key -> new ArrayDeque<>());
		synchronized (submissions) {
			while (!submissions.isEmpty() && submissions.peekFirst().isBefore(windowStart)) {
				submissions.pollFirst();
			}
			if (submissions.size() >= this.contactProperties.maxSubmissionsPerWindow()) {
				Instant oldest = submissions.peekFirst();
				long retryAfterSeconds = oldest == null
						? Math.max(1, this.contactProperties.rateWindow().toSeconds())
						: Math.max(1, Duration.between(now, oldest.plus(this.contactProperties.rateWindow())).toSeconds());
				throw new ContactSubmissionRateLimitException(retryAfterSeconds);
			}
			submissions.addLast(now);
		}
	}

	private void purgeExpiredClients(Instant windowStart) {
		this.recentSubmissions.entrySet().removeIf(entry -> {
			Deque<Instant> timestamps = entry.getValue();
			synchronized (timestamps) {
				while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(windowStart)) {
					timestamps.pollFirst();
				}
				return timestamps.isEmpty();
			}
		});
	}

	private String key(String clientId) {
		return clientId == null || clientId.isBlank() ? "unknown" : clientId.trim();
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
