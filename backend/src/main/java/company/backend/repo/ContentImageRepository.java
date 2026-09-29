package company.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import company.backend.entity.ContentImage;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
}