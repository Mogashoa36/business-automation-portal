package company.backend.dto;

import java.time.Instant;

import company.backend.entity.ActivityLog;

/** One audit trail row, as shown in the admin activity view. */
public record ActivityLogResponse(Long id, Instant occurredAt, String actor, String action, String resource,
		String result, String detail) {

	public static ActivityLogResponse from(ActivityLog entry) {
		return new ActivityLogResponse(entry.getId(), entry.getOccurredAt(), entry.getActor(), entry.getAction(),
				entry.getResource(), entry.getResult(), entry.getDetail());
	}
}