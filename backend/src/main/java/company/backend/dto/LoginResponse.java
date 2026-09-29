package company.backend.dto;

/** Result of a successful sign in. Contains no credential of any kind. */
public record LoginResponse(AdminUserResponse user, boolean mustChangePassword) {

	public LoginResponse(company.backend.service.AdminSession session) {
		this(session.user(), session.mustChangePassword());
	}
}