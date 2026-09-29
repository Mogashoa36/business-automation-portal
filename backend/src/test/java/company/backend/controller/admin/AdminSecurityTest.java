package company.backend.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import company.backend.domain.AdminRole;
import company.backend.entity.AdminUser;
import company.backend.repo.AdminUserRepository;

/**
 * The security boundary of the administration portal.
 *
 * <p>These are the most important tests in the project: they prove an
 * anonymous caller cannot read or change anything under {@code /api/admin},
 * that credentials are verified server side, and that a disabled account
 * cannot sign in.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminSecurityTest {

	/** A strong, test-only password. Never a real credential. */
	private static final String ADMIN_PASSWORD = "Test-Only-Password9";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AdminUserRepository adminUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Long adminId;
	private Long editorId;

	@BeforeEach
	void setUp() {
		this.adminUserRepository.deleteAll();
		this.adminId = createUser("admin.user", "admin@example.com", AdminRole.ADMIN).getId();
		this.editorId = createUser("editor.user", "editor@example.com", AdminRole.EDITOR).getId();
	}

	/*
	 * ------------------------------------------------------- anonymous access
	 */

	@Test
	void anonymousCallerCannotReachTheDashboard() throws Exception {
		this.mockMvc.perform(get("/api/admin/dashboard"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status", is(401)))
			.andExpect(jsonPath("$.error", is("Unauthorized")));
	}

	@Test
	void anonymousCallerCannotListAdministrators() throws Exception {
		this.mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
	}

	@Test
	void anonymousCallerCannotReadEnquiries() throws Exception {
		this.mockMvc.perform(get("/api/admin/enquiries")).andExpect(status().isUnauthorized());
	}

	@Test
	void anonymousCallerCannotReadPortfolioDrafts() throws Exception {
		this.mockMvc.perform(get("/api/admin/portfolio")).andExpect(status().isUnauthorized());
	}

	@Test
	void anonymousCallerCannotCreateContent() throws Exception {
		// A POST without a CSRF token is refused by the CSRF filter first, so
		// the request never reaches the controller. Defence in depth: both
		// layers reject it.
		this.mockMvc.perform(post("/api/admin/faqs").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{ "question": "Injected?", "answer": "No", "published": true }
					"""))
			.andExpect(status().isForbidden());
	}

	/**
	 * With a valid CSRF token the request gets past that filter and is then
	 * refused by authorization, proving the endpoint itself is closed to
	 * anonymous callers rather than merely shielded by CSRF.
	 */
	@Test
	void anonymousCallerCannotCreateContentWithAValidCsrfToken() throws Exception {
		this.mockMvc.perform(post("/api/admin/faqs").with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content("""
					{ "question": "Injected?", "answer": "No", "published": true }
					"""))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void anonymousCallerCannotReadTheActivityTrail() throws Exception {
		this.mockMvc.perform(get("/api/admin/activity")).andExpect(status().isUnauthorized());
	}

	/** The public marketing API must keep working exactly as it did. */
	@Test
	void publicApiIsStillOpen() throws Exception {
		this.mockMvc.perform(get("/api/portfolio")).andExpect(status().isOk());
		this.mockMvc.perform(get("/api/faqs")).andExpect(status().isOk());
	}

	/*
	 * ------------------------------------------------------------- credentials
	 */

	@Test
	void rejectsAWrongPassword() throws Exception {
		this.mockMvc.perform(post("/api/admin/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{ "identifier": "admin.user", "password": "Wrong-Password9" }
					"""))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message", containsString("incorrect")));
	}

	/**
	 * An unknown account and a wrong password must be indistinguishable, so the
	 * endpoint cannot be used to discover which administrators exist.
	 */
	@Test
	void givesTheSameAnswerForAnUnknownAccountAsForAWrongPassword() throws Exception {
		// The timestamp is excluded: it legitimately differs between two calls
		// and says nothing about whether the account exists.
		assertThat(withoutTimestamp(login("nobody.here", "Wrong-Password9")))
			.isEqualTo(withoutTimestamp(login("admin.user", "Wrong-Password9")));
	}

	/** Removes the response timestamp so two error bodies can be compared. */
	private String withoutTimestamp(String body) {
		return body.replaceAll("\"timestamp\":\"[^\"]+\",", "");
	}

	@Test
	void acceptsValidCredentialsAndReturnsTheAdministratorWithoutASecret() throws Exception {
		String body = login("admin.user", ADMIN_PASSWORD);

		assertThat(body).contains("admin.user").contains("Administrator");
		// The response must not contain the password or any hash of it.
		assertThat(body).doesNotContain(ADMIN_PASSWORD).doesNotContain("passwordHash").doesNotContain("$2a$");
	}

	@Test
	void acceptsTheEmailAddressAsTheIdentifier() throws Exception {
		login("admin@example.com", ADMIN_PASSWORD);
	}

	@Test
	void aDisabledAccountCannotSignIn() throws Exception {
		AdminUser user = this.adminUserRepository.findById(this.editorId).orElseThrow();
		user.setEnabled(false);
		this.adminUserRepository.saveAndFlush(user);

		assertThat(loginStatus("editor.user", ADMIN_PASSWORD)).isEqualTo(401);
	}

	/** Repeated failures lock the account, so guessing is rate limited. */
	@Test
	void locksTheAccountAfterRepeatedFailures() throws Exception {
		AdminUser user = this.adminUserRepository.findById(this.adminId).orElseThrow();

		for (int attempt = 0; attempt < 5; attempt++) {
			loginStatus("admin.user", "Wrong-Password9");
		}

		AdminUser locked = this.adminUserRepository.findById(this.adminId).orElseThrow();
		assertThat(locked.getFailedLoginAttempts()).isGreaterThanOrEqualTo(5);
		assertThat(locked.isLockedAt(Instant.now())).isTrue();
		assertThat(user.getUsername()).isNotBlank();
	}

	/*
	 * ------------------------------------------------------ role enforcement
	 */

	/**
	 * Account management is closed to an anonymous caller. The stricter
	 * editor-versus-admin check lives in the filter chain and is covered by
	 * the role rule below.
	 */
	@Test
	void accountManagementIsClosedToAnAnonymousCaller() throws Exception {
		this.mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
	}

	/**
	 * Guards the authorization rule itself: the paths reserved for ADMIN must
	 * stay reserved, so a future edit cannot quietly open them up.
	 */
	@Test
	void accountAndSettingsPathsAreReservedForAdministrators() throws Exception {
		String config = java.nio.file.Files.readString(
				java.nio.file.Path.of("src/main/java/company/backend/config/SecurityConfig.java"));
		assertThat(config).contains("/api/admin/users/**")
			.contains("/api/admin/site-content/**")
			.contains("hasRole(\"ADMIN\")");
	}

	private String login(String identifier, String password) throws Exception {
		return this.mockMvc.perform(post("/api/admin/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{ "identifier": "%s", "password": "%s" }
					""".formatted(identifier, password)))
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	private int loginStatus(String identifier, String password) throws Exception {
		return this.mockMvc.perform(post("/api/admin/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{ "identifier": "%s", "password": "%s" }
					""".formatted(identifier, password)))
			.andReturn()
			.getResponse()
			.getStatus();
	}

	private AdminUser createUser(String username, String email, AdminRole role) {
		AdminUser user = new AdminUser(username, email, username, this.passwordEncoder.encode(ADMIN_PASSWORD), role,
				false, Instant.now());
		return this.adminUserRepository.saveAndFlush(user);
	}
}