package company.backend.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import company.backend.entity.PortfolioProject;
import company.backend.repo.PortfolioProjectRepository;

/**
 * Loads the illustrative portfolio case studies on first start so a fresh
 * database shows a complete website.
 *
 * <p>Every seeded project is flagged as a sample project, which the website
 * renders as an explicit "sample project" label - they are never presented as
 * work delivered for a real client. The seeder is idempotent: it only writes
 * when the table is empty, and it can be switched off entirely with
 * {@code app.seed-sample-data=false}.
 */
@Component
@ConditionalOnProperty(name = "app.seed-sample-data", havingValue = "true", matchIfMissing = true)
public class PortfolioDataSeeder implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(PortfolioDataSeeder.class);

	private static final int SAMPLE_PROJECT_COUNT = 3;

	private final PortfolioProjectRepository portfolioProjectRepository;

	public PortfolioDataSeeder(PortfolioProjectRepository portfolioProjectRepository) {
		this.portfolioProjectRepository = portfolioProjectRepository;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (this.portfolioProjectRepository.count() > 0) {
			return;
		}
		this.portfolioProjectRepository.saveAll(sampleProjects());
		log.info("Seeded {} sample portfolio projects", SAMPLE_PROJECT_COUNT);
	}

	private static List<PortfolioProject> sampleProjects() {
		return List.of(new PortfolioProject("approval-automation", "Approval Automation Platform",
				"Professional Services",
				"A sample scenario showing how a multi-step, email driven approval process becomes a single digital workflow.",
				"Approvals were requested by email, chased manually and tracked in a shared spreadsheet. Nothing showed who was holding up a request, and month end reporting was rebuilt by hand every week.",
				"A configurable workflow engine where requests are raised, routed to the correct approvers, escalated automatically when a step stalls, and closed with a full audit trail. Reporting is generated from the same data instead of a separate spreadsheet.",
				"Java, Spring Boot, Spring Data JPA, Spring Security, PostgreSQL, Angular, TypeScript",
				"Configurable approval steps with conditional routing,Automatic escalation timers,Email and in-app notifications,Complete audit trail of every action,Status dashboards per approver,Exportable management reports",
				"Approvals move in one auditable system instead of email and spreadsheets,Management can see where a request is stuck at any moment,Reporting effort drops from hours per week to a click",
				null, true, true, 1),

				new PortfolioProject("procurement-workflow", "Procurement Workflow System", "Manufacturing",
				"A sample scenario covering purchase requests, approvals, supplier records and delivery tracking in one flow.",
				"Purchase requests were raised on paper or in inboxes, approved inconsistently and then chased by phone. There was no single view of what was ordered, from which supplier, or what had been delivered.",
				"A digital procurement workflow: request, manager approval, procurement review, purchase order, supplier confirmation and delivery completion. Supplier records, approvals and delivery status live in one database that management can report from.",
				"Java, Spring Boot, Spring Data JPA, REST API, PostgreSQL, Angular, TypeScript, AWS",
				"Purchase request forms with line items,Multi level approval rules,Supplier management records,Purchase order generation,Delivery and receipt tracking,Spend reporting by category and supplier",
				"A clear, auditable trail from request to delivery,Less time spent chasing approvals,Supplier and spend information available in one place",
				null, true, true, 2),

				new PortfolioProject("operations-dashboard", "Operations Reporting Dashboard", "Logistics",
				"A sample scenario that turns scattered spreadsheets into operational dashboards and routine reports.",
				"Operational data lived in several spreadsheets that were updated at different times. Managers worked from numbers that were already out of date, and every report was produced manually.",
				"A secure web application that collects operational data once, validates it, and publishes role based dashboards for management and operations, with scheduled and on demand report exports.",
				"Java, Spring Boot, Spring Security, REST API, PostgreSQL, Angular, TypeScript, Chart based data visualisation",
				"Role based dashboards,Live and scheduled reports,Export to spreadsheet and PDF,Data validation on entry,User authentication with access control,Audit history of changes",
				"Managers work from current information rather than yesterday's spreadsheet,Report preparation becomes repeatable rather than manual,Data quality improves through validation at source",
				null, true, true, 3));
	}
}
