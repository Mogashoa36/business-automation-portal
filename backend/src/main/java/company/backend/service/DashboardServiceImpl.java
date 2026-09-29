package company.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.domain.EnquiryStatus;
import company.backend.dto.ActivityLogResponse;
import company.backend.dto.DashboardResponse;
import company.backend.repo.ActivityLogRepository;
import company.backend.repo.AdminUserRepository;
import company.backend.repo.ContactMessageRepository;
import company.backend.repo.FaqItemRepository;
import company.backend.repo.PortfolioProjectRepository;

/**
 * Dashboard numbers, all derived from live counts.
 *
 * <p>Services and solutions are static marketing content in the frontend, so
 * the counts the website already advertises are used rather than inventing a
 * table for them. Everything else is a real database count.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

	/** Matches the three services the website publishes. */
	private static final int PUBLISHED_SERVICE_COUNT = 3;

	/** Matches the five solutions the website publishes. */
	private static final int PUBLISHED_SOLUTION_COUNT = 5;

	private static final int RECENT_ACTIVITY_LIMIT = 8;

	private final PortfolioProjectRepository portfolioProjectRepository;
	private final FaqItemRepository faqItemRepository;
	private final ContactMessageRepository contactMessageRepository;
	private final AdminUserRepository adminUserRepository;
	private final ActivityLogRepository activityLogRepository;

	public DashboardServiceImpl(PortfolioProjectRepository portfolioProjectRepository,
			FaqItemRepository faqItemRepository, ContactMessageRepository contactMessageRepository,
			AdminUserRepository adminUserRepository, ActivityLogRepository activityLogRepository) {
		this.portfolioProjectRepository = portfolioProjectRepository;
		this.faqItemRepository = faqItemRepository;
		this.contactMessageRepository = contactMessageRepository;
		this.adminUserRepository = adminUserRepository;
		this.activityLogRepository = activityLogRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public DashboardResponse summary() {
		long portfolioProjects = this.portfolioProjectRepository.count();
		long publishedProjects = this.portfolioProjectRepository.countByPublishedTrue();
		long faqs = this.faqItemRepository.count();
		long publishedFaqs = this.faqItemRepository.findByPublishedTrueOrderByDisplayOrderAsc().size();
		long enquiries = this.contactMessageRepository.count();
		long newEnquiries = this.contactMessageRepository.countByStatus(EnquiryStatus.NEW);
		long activeEnquiries = this.contactMessageRepository.countByStatus(EnquiryStatus.IN_PROGRESS)
				+ this.contactMessageRepository.countByStatus(EnquiryStatus.CONTACTED);

		DashboardResponse.Counts counts = new DashboardResponse.Counts(portfolioProjects, publishedProjects,
				portfolioProjects - publishedProjects, PUBLISHED_SERVICE_COUNT, PUBLISHED_SOLUTION_COUNT, faqs,
				publishedFaqs, enquiries, newEnquiries, activeEnquiries, this.adminUserRepository.count(),
				publishedProjects + publishedFaqs);

		java.util.List<ActivityLogResponse> recent = this.activityLogRepository
			.findTop8ByOrderByOccurredAtDesc()
			.stream()
			.map(ActivityLogResponse::from)
			.toList();

		return new DashboardResponse(counts, recent);
	}
}