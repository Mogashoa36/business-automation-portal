package company.backend.service;

import java.util.List;

import company.backend.dto.ActivityLogResponse;

/**
 * Audit trail for administrative actions.
 */
public interface ActivityLogService {

	/**
	 * Records an action. Implementations must never be given a password, token
	 * or any other secret, and the detail is truncated server side.
	 */
	void record(String actor, String action, String resource, String result, String detail);

	/** Most recent first. */
	List<ActivityLogResponse> recent(int limit);
}