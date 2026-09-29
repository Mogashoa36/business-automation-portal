package company.backend.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import company.backend.entity.SiteContent;

public interface SiteContentRepository extends JpaRepository<SiteContent, Long> {

	Optional<SiteContent> findByContentKey(String contentKey);
}