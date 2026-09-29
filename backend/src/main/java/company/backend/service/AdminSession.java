package company.backend.service;

import java.time.Instant;

import company.backend.domain.AdminRole;
import company.backend.dto.AdminUserResponse;

/** The outcome of a successful sign in, safe to return to the client. */
public record AdminSession(AdminUserResponse user, Instant signedInAt, boolean mustChangePassword) {

	public AdminSession(AdminUserResponse user) {
		this(user, Instant.now(), user.mustChangePassword());
	}

	public boolean isAdmin() {
		return this.user.role() == AdminRole.ADMIN;
	}
}