package company.backend.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import company.backend.entity.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	/** Most recent first, paginated so the admin table never loads everything. */
	Page<ActivityLog> findAllByOrderByOccurredAtDesc(Pageable pageable);

	List<ActivityLog> findTop8ByOrderByOccurredAtDesc();
}