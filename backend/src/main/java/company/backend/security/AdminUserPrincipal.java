package company.backend.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import company.backend.domain.AdminRole;
import company.backend.entity.AdminUser;

/**
 * Adapts an {@link AdminUser} to Spring Security.
 *
 * <p>The entity is never handed to the controller layer directly; this wrapper
 * exists so the password hash cannot be accidentally serialised into a
 * response body.
 */
public class AdminUserPrincipal implements UserDetails {

	private final Long id;
	private final String username;
	private final String email;
	private final String displayName;
	private final String passwordHash;
	private final AdminRole role;
	private final boolean enabled;
	private final boolean mustChangePassword;

	public AdminUserPrincipal(AdminUser user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.displayName = user.getDisplayName();
		this.passwordHash = user.getPasswordHash();
		this.role = user.getRole();
		this.enabled = user.isEnabled();
		this.mustChangePassword = user.isMustChangePassword();
	}

	public Long getId() {
		return this.id;
	}

	public String getEmail() {
		return this.email;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public AdminRole getRole() {
		return this.role;
	}

	public boolean isMustChangePassword() {
		return this.mustChangePassword;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Spring Security's hasRole("ADMIN") expects the ROLE_ prefix.
		return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
	}

	@Override
	public String getPassword() {
		return this.passwordHash;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// Locking is enforced in the authentication service before the
		// credentials are ever compared, so the principal reports unlocked here.
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
}