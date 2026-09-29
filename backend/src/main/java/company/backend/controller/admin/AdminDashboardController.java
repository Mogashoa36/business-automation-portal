package company.backend.controller.admin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.backend.dto.ActivityLogResponse;
import company.backend.dto.DashboardResponse;
import company.backend.service.ActivityLogService;
import company.backend.service.DashboardService;

/**
 * Dashboard summary and the activity trail.
 *
 * <p>Every figure on the dashboard is a live database count; an empty
 * installation reports zeros rather than invented numbers.
 */
@RestController
@RequestMapping(path = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminDashboardController {

	private final DashboardService dashboardService;
	private final ActivityLogService activityLogService;

	public AdminDashboardController(DashboardService dashboardService, ActivityLogService activityLogService) {
		this.dashboardService = dashboardService;
		this.activityLogService = activityLogService;
	}

	@GetMapping("/dashboard")
	public DashboardResponse dashboard() {
		return this.dashboardService.summary();
	}

	/** Recent audit entries, newest first. */
	@GetMapping("/activity")
	public List<ActivityLogResponse> activity() {
		return this.activityLogService.recent(100);
	}
}