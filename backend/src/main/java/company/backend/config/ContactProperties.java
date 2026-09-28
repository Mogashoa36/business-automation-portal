package company.backend.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Abuse protection settings for the public contact endpoint. Configured through
 * {@code app.contact.*} so limits can be tuned per environment without code
 * changes.
 *
 * @param maxSubmissionsPerWindow how many messages a single client may submit
 * inside the window
 * @param rateWindow length of the sliding window
 */
@ConfigurationProperties(prefix = "app.contact")
public record ContactProperties(int maxSubmissionsPerWindow, Duration rateWindow) {

	public ContactProperties {
		if (maxSubmissionsPerWindow < 1) {
			maxSubmissionsPerWindow = 1;
		}
		if (rateWindow == null || rateWindow.isNegative() || rateWindow.isZero()) {
			rateWindow = Duration.ofMinutes(10);
		}
	}
}
