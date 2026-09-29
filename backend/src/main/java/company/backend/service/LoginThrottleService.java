package company.backend.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import company.backend.config.AdminSecurityProperties;

/**
 * Login throttling.
 *
 * <p>Two independent limits work together: a per client sliding window stops one
 * address spraying passwords, and the per account failure counter (held on
 * {@code AdminUser}) stops one account being attacked from many addresses.
 *
 * <p>This mirrors the sliding window approach already used by the public
 * contact endpoint, so the application has one abuse protection idiom rather
 * than two. State is in memory, which suits a single node deployment; behind a
 * load balancer the account lockout still applies, and the per client window
 * can be moved to shared storage.
 */
@Service
public class LoginThrottleService {

	private static final int MAX_TRACKED_CLIENTS = 5000;

	private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();
	private final int maxAttempts;
	private final Duration window;

	public LoginThrottleService(AdminSecurityProperties properties) {
		this.maxAttempts = Math.max(3, properties.maxFailedAttempts() * 2);
		this.window = Duration.ofMinutes(10);
	}

	/**
	 * Records a failed attempt and reports whether this client is now throttled.
	 * @return the number of seconds to wait, or {@code 0} when the client may
	 * still attempt a sign in
	 */
	public long recordFailure(String clientId) {
		Instant now = Instant.now();
		Instant windowStart = now.minus(this.window);

		if (this.attempts.size() > MAX_TRACKED_CLIENTS) {
			this.attempts.entrySet().removeIf(entry -> isEmpty(entry.getValue(), windowStart));
		}

		Deque<Instant> history = this.attempts.computeIfAbsent(key(clientId), ignored -> new ArrayDeque<>());
		synchronized (history) {
			prune(history, windowStart);
			history.addLast(now);
			if (history.size() < this.maxAttempts) {
				return 0L;
			}
			Instant oldest = history.peekFirst();
			return oldest == null ? 0L : Math.max(1L, Duration.between(now, oldest.plus(this.window)).toSeconds());
		}
	}

	/** Clears the client counter after a successful sign in. */
	public void recordSuccess(String clientId) {
		this.attempts.remove(key(clientId));
	}

	/** Throttles a client that is already over the limit, before verifying. */
	public long retryAfterSeconds(String clientId) {
		Instant now = Instant.now();
		Deque<Instant> history = this.attempts.get(key(clientId));
		if (history == null) {
			return 0L;
		}
		synchronized (history) {
			prune(history, now.minus(this.window));
			if (history.size() < this.maxAttempts) {
				return 0L;
			}
			Instant oldest = history.peekFirst();
			return oldest == null ? 0L : Math.max(1L, Duration.between(now, oldest.plus(this.window)).toSeconds());
		}
	}

	private void prune(Deque<Instant> history, Instant windowStart) {
		while (!history.isEmpty() && history.peekFirst().isBefore(windowStart)) {
			history.pollFirst();
		}
	}

	private boolean isEmpty(Deque<Instant> history, Instant windowStart) {
		synchronized (history) {
			prune(history, windowStart);
			return history.isEmpty();
		}
	}

	private String key(String clientId) {
		return clientId == null || clientId.isBlank() ? "unknown" : clientId.trim().toLowerCase(Locale.ROOT);
	}
}