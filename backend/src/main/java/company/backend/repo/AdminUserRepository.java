package company.backend.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import company.backend.entity.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

	/** Sign in accepts either the username or the email address. */
	Optional<AdminUser> findByUsername(String username);

	Optional<AdminUser> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}