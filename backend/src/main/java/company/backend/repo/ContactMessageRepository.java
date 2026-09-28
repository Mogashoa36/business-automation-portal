package company.backend.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import company.backend.entity.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

	Optional<ContactMessage> findByReference(String reference);

	boolean existsByReference(String reference);
}
