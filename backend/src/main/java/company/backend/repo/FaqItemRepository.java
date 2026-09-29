package company.backend.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import company.backend.entity.FaqItem;

public interface FaqItemRepository extends JpaRepository<FaqItem, Long> {

	/** Public website content: published entries in their display order. */
	List<FaqItem> findByPublishedTrueOrderByDisplayOrderAsc();

	Page<FaqItem> findAllByOrderByDisplayOrderAsc(Pageable pageable);
}