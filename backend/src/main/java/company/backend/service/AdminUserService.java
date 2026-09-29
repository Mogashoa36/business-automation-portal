package company.backend.service;

import java.util.List;

import company.backend.dto.AdminUserCreateRequest;
import company.backend.dto.AdminUserResponse;
import company.backend.dto.AdminUserUpdateRequest;

/** Administrator account management. Restricted to the ADMIN role. */
public interface AdminUserService {

	List<AdminUserResponse> findAll();

	AdminUserResponse create(String actor, AdminUserCreateRequest request);

	AdminUserResponse update(String actor, Long id, AdminUserUpdateRequest request);

	/**
	 * Replaces a user's password with a new one and forces a change at next
	 * sign in. The previous password is never revealed, because only a hash
	 * is stored.
	 */
	AdminUserResponse resetPassword(String actor, Long id, String newPassword);

	void delete(String actor, Long id);
}