package company.backend.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import company.backend.dto.ActivityLogResponse;
import company.backend.entity.ActivityLog;
import company.backend.repo.ActivityLogRepository;

/**
 * Writes and reads the administrative audit trail.
 *
 * <p>Recording is deliberately in its own transaction so an audit entry is
 * still written when the surrounding business transaction rolls back - a
 * rejected action is exactly the kind of event worth keeping.
 */
@Service
public class ActivityLogServiceImpl implements ActivityLogService {

	private static final int MAX_LIMIT = 100;
	private static final int MAX_DETAIL_LENGTH = 400;

	private final ActivityLogRepository activityLogRepository;

	public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
		this.activityLogRepository = activityLogRepository;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void record(String actor, String action, String resource, String result, String detail) {
		try {
			this.activityLogRepository.save(
					new ActivityLog(safe(actor), safe(action), safe(resource), safe(result), truncate(detail),
							java.time.Instant.now()));
		}
		catch (RuntimeException exception) {
			// A failure to write the audit trail must never break the operation
			// the administrator actually asked for.
			org.slf4j.LoggerFactory.getLogger(ActivityLogServiceImpl.class)
				.warn("Could not record an activity log entry for action {}", safe(action));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityLogResponse> recent(int limit) {
		int bounded = Math.max(1, Math.min(limit, MAX_LIMIT));
		return this.activityLogRepository.findAllByOrderByOccurredAtDesc(PageRequest.of(0, bounded))
			.stream()
			.map(ActivityLogResponse::from)
			.toList();
	}

	private String truncate(String detail) {
		if (detail == null) {
			return null;
		}
		String trimmed = detail.trim();
		if (trimmed.isEmpty()) {
			return null;
		}
		return trimmed.length() <= MAX_DETAIL_LENGTH ? trimmed : trimmed.substring(0, MAX_DETAIL_LENGTH);
	}

	private String safe(String value) {
		return value == null ? "system" : value.trim();
	}
}