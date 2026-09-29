package company.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import company.backend.security.AdminAccessDeniedHandler;
import company.backend.security.AdminAuthenticationEntryPoint;
import company.backend.security.AdminUserDetailsService;

/**
 * Security for the whole application.
 *
 * <p>Two clearly separated surfaces:
 * <ul>
 * <li>The public API ({@code /api/contact}, {@code /api/portfolio},
 * {@code /api/faqs}) stays open, exactly as it was before administration
 * existed.</li>
 * <li>Everything under {@code /api/admin/**} requires an authenticated session
 * and is authorized by role on the server. Anonymous callers receive a JSON
 * {@code 401} and insufficiently privileged callers a JSON {@code 403}, both in
 * the existing {@code ApiErrorResponse} shape.</li>
 * </ul>
 *
 * <p>Sessions are held server side. The browser only ever holds an opaque
 * {@code HttpOnly} session cookie, so no credential can be read from storage or
 * exfiltrated through script. CSRF uses Spring Security's cookie backed double
 * submit token, which the Angular {@code HttpClient} reads and echoes
 * automatically.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final AdminSecurityProperties adminSecurityProperties;

	public SecurityConfig(AdminSecurityProperties adminSecurityProperties) {
		this.adminSecurityProperties = adminSecurityProperties;
	}

	/**
	 * BCrypt at strength 12. Every stored administrator password is a hash; the
	 * plaintext is never persisted, returned or logged.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		// Hides whether an account exists, so probing cannot enumerate accounts.
		provider.setHideUserNotFoundExceptions(true);
		return new ProviderManager(provider);
	}

	/**
	 * Stores the security context in the HTTP session so logout invalidates it
	 * server side rather than merely asking the browser to forget a token.
	 */
	@Bean
	public SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http,
			SecurityContextRepository securityContextRepository, AdminAuthenticationEntryPoint entryPoint,
			AdminAccessDeniedHandler accessDeniedHandler) throws Exception {

		// Double submit cookie: readable by the Angular client so it can echo it
		// back in the X-XSRF-TOKEN header on every state changing request.
		CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
		csrfTokenRepository.setCookiePath("/");

		return http.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository)
			/*
			 * CSRF protection is scoped to the administration surface, which is
			 * the only place a request carries ambient authority (the session
			 * cookie).
			 *
			 * The public contact form is excluded because it is stateless: the
			 * caller is anonymous and sends no cookie, so there is nothing for
			 * a cross site page to ride on. Excluding it is what keeps the
			 * public contract unchanged.
			 */
				.ignoringRequestMatchers("/api/admin/auth/login", "/api/contact"))
			// A new session id on every privilege change defeats session fixation.
			.sessionManagement(session -> session.sessionFixation(fixation -> fixation.changeSessionId())
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
			.authorizeHttpRequests(requests -> requests
				// Public marketing and contact endpoints, unchanged.
				.requestMatchers(HttpMethod.GET, "/api/portfolio", "/api/portfolio/**", "/api/faqs").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
				// Uploaded images are public, exactly like the rest of the marketing
				// content that references them. They are read only, and served with
				// a fixed content type and nosniff by the controller.
				.requestMatchers(HttpMethod.GET, "/api/images/**").permitAll()
				// Sign in must be reachable without a session.
				.requestMatchers("/api/admin/auth/login").permitAll()
				// Account administration, site settings and image uploads are
				// ADMIN only. An editor manages content, not the system.
				.requestMatchers("/api/admin/users/**", "/api/admin/site-content/**", "/api/admin/images/**")
					.hasRole("ADMIN")
				.requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "EDITOR")
				// Anything else requires a session.
				.anyRequest().authenticated())
			.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint)
				.accessDeniedHandler(accessDeniedHandler))
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			// Defence in depth for a JSON API: no framing, no MIME sniffing and
			// no referrer leakage from the administration UI.
			.headers(headers -> headers.frameOptions(frame -> frame.deny())
				.referrerPolicy(referrer -> referrer.policy(
						org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)))
			.securityContext(context -> context.securityContextRepository(securityContextRepository))
			// The API answers with JSON status codes and never redirects to a
			// login page, which would confuse the client side router.
			.logout(logout -> logout.disable())
			.build();
	}

	/**
	 * CORS for the API. Credentials are allowed so the session cookie is
	 * sent, which requires listing origins explicitly rather than using the
	 * {@code *} wildcard.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(this.adminSecurityProperties.allowedOrigins());
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Content-Type", "Accept", "X-XSRF-TOKEN", "X-Requested-With"));
		configuration.setExposedHeaders(List.of("X-CSRF-TOKEN"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		return source;
	}
}
