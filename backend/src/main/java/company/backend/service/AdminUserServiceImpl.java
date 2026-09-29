package company.backend.service;

import java.time.Instant;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.domain.AdminRole;
import company.backend.dto.AdminUserCreateRequest;
import company.backend.dto.AdminUserResponse;
import company.backend.dto.AdminUserUpdateRequest;
import company.backend.entity.AdminUser;
import company.backend.exception.InvalidRequestException;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.AdminUserRepository;
import company.backend.security.PasswordPolicy;

/**
 * Administrator account management.
 *
 * <p>Two invariants protect against locking everyone out: the last enabled
 * ADMIN cannot be demoted, disabled or deleted, and nobody can disable or
 * remove their own account.
 *
 * <p>Passwords are validated against {@link PasswordPolicy} and stored only as
 * BCrypt hashes. No method here returns or logs a password.
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

	private final AdminUserRepository adminUserRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminUserServiceImpl(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
		this.adminUserRepository = adminUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AdminUserResponse> findAll() {
		return this.adminUserRepository.findAll().stream().map(AdminUserResponse::of).toList();
	}

	@Override
	@Transactional
	public AdminUserResponse create(String actor, AdminUserCreateRequest request) {
		String username = AdminUser.normaliseUsername(request.username());
		String email = AdminUser.normaliseEmail(request.email());

		if (!AdminUser.isValidUsername(username)) {
			throw new InvalidRequestException(
					"Usernames may only contain letters, numbers, dots, underscores and hyphens.");
		}
		if (this.adminUserRepository.existsByUsername(username)) {
			throw new InvalidRequestException("That username is already taken.");
		}
		if (this.adminUserRepository.existsByEmail(email)) {
			throw new InvalidRequestException("That email address is already registered to an administrator.");
		}

		String policyError = PasswordPolicy.validate(request.temporaryPassword());
		if (policyError != null) {
			throw new InvalidRequestException(policyError);
		}

		// mustChangePassword is true: a temporary secret is rotated at first
		// sign in rather than becoming a long lived credential.
		AdminUser created = new AdminUser(username, email, request.displayName().trim(),
				this.passwordEncoder.encode(request.temporaryPassword()), request.role(), true, Instant.now());

		return AdminUserResponse.of(this.adminUserRepository.save(created));
	}

	@Override
	@Transactional
	public AdminUserResponse update(String actor, Long id, AdminUserUpdateRequest request) {
		AdminUser user = require(id);
		String email = AdminUser.normaliseEmail(request.email());

		this.adminUserRepository.findByEmail(email)
			.filter(other -> !other.getId().equals(id))
			.ifPresent(other -> {
				throw new InvalidRequestException("That email address already belongs to another administrator.");
			});

		boolean losingAdmin = user.getRole() == AdminRole.ADMIN
				&& (request.role() != AdminRole.ADMIN || !Boolean.TRUE.equals(request.enabled()));
		if (losingAdmin) {
			guardLastAdministrator(user);
		}
		if (isSelf(actor, user) && !Boolean.TRUE.equals(request.enabled())) {
			throw new InvalidRequestException("You cannot disable your own account.");
		}

		user.updateEmail(email);
		user.setRole(request.role());
		user.setEnabled(Boolean.TRUE.equals(request.enabled()));

		return AdminUserResponse.of(this.adminUserRepository.save(user));
	}

	@Override
	@Transactional
	public AdminUserResponse resetPassword(String actor, Long id, String newPassword) {
		AdminUser user = require(id);

		String policyError = PasswordPolicy.validate(newPassword);
		if (policyError != null) {
			throw new InvalidRequestException(policyError);
		}

		user.setPasswordHash(this.passwordEncoder.encode(newPassword));
		// Flagged so the new secret is rotated by its owner at next sign in.
		user.forcePasswordChangeOnNextLogin();
		user.resetFailedLogins();

		// The supplied password is never logged or returned.
		return AdminUserResponse.of(this.adminUserRepository.save(user));
	}

	@Override
	@Transactional
	public void delete(String actor, Long id) {
		AdminUser user = require(id);
		if (isSelf(actor, user)) {
			throw new InvalidRequestException("You cannot remove your own account.");
		}
		if (user.getRole() == AdminRole.ADMIN) {
			guardLastAdministrator(user);
		}
		this.adminUserRepository.delete(user);
	}

	/**
	 * Refuses an operation that would leave the portal with no enabled
	 * administrator.
	 */
	private void guardLastAdministrator(AdminUser user) {
		long remaining = this.adminUserRepository.findAll()
			.stream()
			.filter(other -> other.getRole() == AdminRole.ADMIN && other.isEnabled())
			.filter(other -> !other.getId().equals(user.getId()))
			.count();
		if (remaining == 0) {
			throw new InvalidRequestException("This is the only enabled administrator. Create another one first.");
		}
	}

	private boolean isSelf(String actor, AdminUser user) {
		return actor != null && actor.equalsIgnoreCase(user.getUsername());
	}

	private AdminUser require(Long id) {
		return this.adminUserRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No administrator found with id " + id + "."));
	}
}