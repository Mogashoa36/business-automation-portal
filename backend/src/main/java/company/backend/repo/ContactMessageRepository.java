package company.backend.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import company.backend.domain.EnquiryStatus;
import company.backend.entity.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

	Optional<ContactMessage> findByReference(String reference);

	boolean existsByReference(String reference);

	/*
	 * Administration queries. Enquiries are private, so these are only ever
	 * reached from an authenticated admin endpoint. Search and status filtering
	 * run in the database so the admin table stays responsive as the table grows.
	 */
	@Query("""
			select m from ContactMessage m
			where (:status is null or m.status = :status)
			  and (:search is null or lower(m.fullName) like :search
			                   or lower(m.email) like :search
			                   or lower(m.company) like :search
			                   or lower(m.reference) like :search)
			order by m.createdAt desc
			""")
	Page<ContactMessage> search(@Param("status") EnquiryStatus status, @Param("search") String search,
			Pageable pageable);

	long countByStatus(EnquiryStatus status);
}
