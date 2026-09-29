package company.backend.security;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.entity.AdminUser;
import company.backend.repo.AdminUserRepository;

/**
 * Loads administrators for Spring Security.
 *
 * <p>Sign in accepts either the username or the email address. The lookup is
 * case insensitive because both columns are stored lower case.
 */
@Service
public class AdminUserDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(AdminUserDetailsService.class);

	private final AdminUserRepository adminUserRepository;

	public AdminUserDetailsService(AdminUserRepository adminUserRepository) {
		this.adminUserRepository = adminUserRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		String candidate = identifier == null ? "" : identifier.trim().toLowerCase(Locale.ROOT);
		if (candidate.isEmpty()) {
			throw new UsernameNotFoundException("No administrator supplied");
		}

		AdminUser user = this.adminUserRepository.findByUsername(candidate)
			.or(() -> this.adminUserRepository.findByEmail(candidate))
			.orElseThrow(() -> new UsernameNotFoundException("No administrator found"));

		// Deliberately no logging of the identifier or of any credential.
		log.debug("Loaded administrator principal for an admin sign in attempt");
		return new AdminUserPrincipal(user);
	}
}