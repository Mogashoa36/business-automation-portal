package company.backend.service;

import company.backend.dto.DashboardResponse;

/** Builds the admin dashboard summary from real counts. */
public interface DashboardService {

	DashboardResponse summary();
}