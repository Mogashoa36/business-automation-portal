package company.backend.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import company.backend.domain.AdminRole;
import company.backend.entity.AdminUser;
import company.backend.repo.AdminUserRepository;
import company.backend.repo.ContentImageRepository;
import company.backend.service.ImageValidator;

/**
 * Security and correctness of administrator image uploads.
 *
 * <p>Uploads are the one place a visitor can push arbitrary bytes onto the
 * server, so every case here is treated as hostile: renamed scripts, wrong
 * magic bytes, oversized files, traversal in the filename, and callers without
 * the right role.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "app.images.directory=${java.io.tmpdir}/bizflow-test-uploads")
class AdminImageSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AdminUserRepository adminUserRepository;

	@Autowired
	private ContentImageRepository imageRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		this.adminUserRepository.deleteAll();
		this.imageRepository.deleteAll();
		createUser("image.admin", AdminRole.ADMIN);
	}

	/* ------------------------------------------------------ authorization */

	@Test
	@DisplayName("an anonymous caller cannot list images")
	void anonymousCallerCannotListImages() throws Exception {
		this.mockMvc.perform(get("/api/admin/images")).andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("an anonymous caller cannot upload")
	void anonymousCallerCannotUpload() throws Exception {
		// The CSRF filter refuses a POST with no token before authentication is
		// considered, so this is a 403 rather than a 401. Either way the request
		// never reaches the controller.
		this.mockMvc.perform(multipart("/api/admin/images").file(realPng()).param("altText", "Something"))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("an editor cannot upload, because uploads are ADMIN only")
	void editorCannotUpload() throws Exception {
		this.mockMvc.perform(multipart("/api/admin/images")
			.file(realPng())
			.param("altText", "Something")
			.with(user("editor.user").roles("EDITOR"))
			.with(csrf()))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("an editor cannot list the image library")
	void editorCannotListImages() throws Exception {
		this.mockMvc.perform(get("/api/admin/images").with(user("editor.user").roles("EDITOR")))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("an editor cannot delete an image")
	void editorCannotDelete() throws Exception {
		this.mockMvc.perform(delete("/api/admin/images/1").with(user("editor.user").roles("EDITOR")).with(csrf()))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("the image upload path stays reserved for administrators")
	void uploadPathIsReservedForAdministrators() throws Exception {
		String config = Files.readString(Path.of("src/main/java/company/backend/config/SecurityConfig.java"));
		assertThat(config).contains("/api/admin/images/**").contains("hasRole(\"ADMIN\")");
	}

	/* ------------------------------------------------------ file validation */

	@Test
	@DisplayName("a real PNG is accepted and recorded with its true dimensions")
	void realPngIsAccepted() throws Exception {
		this.mockMvc
			.perform(multipart("/api/admin/images")
				.file(realPng())
				.param("altText", "A workflow showing an approved purchase request")
				.with(user("image.admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.mimeType", is("image/png")))
			// The dimensions come from decoding the image, not from the request.
			.andExpect(jsonPath("$.width", is(24)))
			.andExpect(jsonPath("$.height", is(16)))
			.andExpect(jsonPath("$.url", startsWith("/api/images/")))
			.andExpect(jsonPath("$.originalName", is("screenshot.png")));
	}

	@Test
	@DisplayName("a script renamed to .png is rejected on its magic bytes")
	void renamedScriptIsRejected() throws Exception {
		MockMultipartFile disguised = new MockMultipartFile("file", "totally-a-photo.png", "image/png",
				"#!/bin/sh\nrm -rf /\n".getBytes(StandardCharsets.UTF_8));

		this.mockMvc.perform(multipart("/api/admin/images")
			.file(disguised)
			.param("altText", "x")
			.with(user("image.admin").roles("ADMIN"))
			.with(csrf()))
			.andExpect(status().is4xxClientError())
			.andExpect(content().string(containsString("not a valid image")));
	}

	@Test
	@DisplayName("an HTML document is rejected even though the bytes are plain text")
	void htmlUploadIsRejected() throws Exception {
		MockMultipartFile html = new MockMultipartFile("file", "page.png", "image/png",
				"<html><script>alert(1)</script></html>".getBytes(StandardCharsets.UTF_8));

		this.mockMvc.perform(multipart("/api/admin/images")
			.file(html)
			.param("altText", "x")
			.with(user("image.admin").roles("ADMIN"))
			.with(csrf()))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("a disallowed MIME type is refused by the allow list")
	void executableMimeTypeIsRefused() throws Exception {
		MockMultipartFile jar = new MockMultipartFile("file", "app.jar", "application/java-archive",
				new byte[] { 0x50, 0x4B, 0x03, 0x04 });

		this.mockMvc.perform(multipart("/api/admin/images")
			.file(jar)
			.param("altText", "x")
			.with(user("image.admin").roles("ADMIN"))
			.with(csrf()))
			.andExpect(status().is4xxClientError())
			.andExpect(content().string(containsString("not supported")));
	}

	@Test
	@DisplayName("an SVG is refused, because it can carry script")
	void svgIsRefused() throws Exception {
		MockMultipartFile svg = new MockMultipartFile("file", "logo.svg", "image/svg+xml",
				"<svg xmlns='http://www.w3.org/2000/svg'><script>alert(1)</script></svg>"
					.getBytes(StandardCharsets.UTF_8));

		this.mockMvc.perform(multipart("/api/admin/images")
			.file(svg)
			.param("altText", "x")
			.with(user("image.admin").roles("ADMIN"))
			.with(csrf()))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("an oversized file is refused")
	void oversizedFileIsRefused() throws Exception {
		byte[] bytes = new byte[6 * 1024 * 1024];
		byte[] header = pngBytes(4, 4);
		System.arraycopy(header, 0, bytes, 0, header.length);

		MockMultipartFile big = new MockMultipartFile("file", "huge.png", "image/png", bytes);
		this.mockMvc.perform(multipart("/api/admin/images")
			.file(big)
			.param("altText", "x")
			.with(user("image.admin").roles("ADMIN"))
			.with(csrf()))
			.andExpect(status().is4xxClientError())
			.andExpect(content().string(containsString("smaller")));
	}

	@Test
	@DisplayName("an upload without alt text is refused, so images are never unlabelled")
	void altTextIsRequired() throws Exception {
		this.mockMvc
			.perform(multipart("/api/admin/images")
				.file(realPng())
				.with(user("image.admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().is4xxClientError())
			.andExpect(content().string(containsString("Alt text")));
	}

	/* --------------------------------------------------------- path safety */

	@Test
	@DisplayName("a traversal filename never reaches the filesystem")
	void traversalFilenameCannotEscapeTheUploadDirectory() throws Exception {
		MockMultipartFile nasty = new MockMultipartFile("file", "../../../evil.png", "image/png",
				pngBytes(8, 8));

		this.mockMvc
			.perform(multipart("/api/admin/images")
				.file(nasty)
				.param("altText", "Traversal attempt")
				.with(user("image.admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isCreated())
			// Whatever the caller sent, the stored name is a bare generated file.
			.andExpect(jsonPath("$.url", matchesPattern("/api/images/[a-f0-9]+\\.png")));

		assertThat(this.imageRepository.findAll()).hasSize(1);
		assertThat(this.imageRepository.findAll().get(0).getFilename()).doesNotContain("..").doesNotContain("/");
	}

	@Test
	@DisplayName("the sanitised display name keeps only a harmless leaf")
	void sanitisedFileNameDropsDirectories() {
		// Only the final path segment survives, so no directory can be forged.
		assertThat(ImageValidator.sanitiseFileName("../../etc/passwd")).isEqualTo("passwd");
		assertThat(ImageValidator.sanitiseFileName("C:\\Windows\\System32\\cmd.exe")).isEqualTo("cmd.exe");
		// A name made only of dots collapses to the fallback rather than "..".
		assertThat(ImageValidator.sanitiseFileName("..")).isEqualTo("image");
		assertThat(ImageValidator.sanitiseFileName("....//....//etc\\passwd")).doesNotContain("/");
	}

	@Test
	@DisplayName("a public image request for a traversal path never returns a file")
	void traversalPathIsNotServed() throws Exception {
		// The container rejects an encoded traversal outright (400) before Spring
		// routes it, and a bare ".." resolves to nothing (404). What matters is
		// that neither can read a file outside the upload directory.
		this.mockMvc.perform(get("/api/images/..%2F..%2Fapplication.properties"))
			.andExpect(status().is4xxClientError());
		this.mockMvc.perform(get("/api/images/..")).andExpect(status().is4xxClientError());
		// A name that simply does not exist is a clean 404 from the service.
		this.mockMvc.perform(get("/api/images/00000000000000000000000000000000.png"))
			.andExpect(status().isNotFound());
	}

	/* --------------------------------------------------- response hardening */

	@Test
	@DisplayName("a served image is nosniff, inline and restricted by CSP")
	void servedImagesAreHardened() throws Exception {
		uploadOne();

		Path stored = Path.of(System.getProperty("java.io.tmpdir"), "bizflow-test-uploads",
				this.imageRepository.findAll().get(0).getFilename());
		assertThat(Files.exists(stored)).isTrue();

		this.mockMvc.perform(get("/api/images/" + stored.getFileName()))
			.andExpect(status().isOk())
			.andExpect(header().string("X-Content-Type-Options", "nosniff"))
			.andExpect(header().string("Content-Security-Policy", containsString("default-src 'none'")))
			.andExpect(header().string("Content-Disposition", containsString("inline")));
	}

	/* -------------------------------------------------------------- helpers */

	private void uploadOne() throws Exception {
		this.mockMvc
			.perform(multipart("/api/admin/images")
				.file(realPng())
				.param("altText", "A dashboard")
				.with(user("image.admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isCreated());
	}

	private MockMultipartFile realPng() throws Exception {
		return new MockMultipartFile("file", "screenshot.png", "image/png", pngBytes(24, 16));
	}

	/** A genuinely encoded PNG, produced rather than hard coded. */
	private byte[] pngBytes(int width, int height) throws Exception {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		graphics.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		return out.toByteArray();
	}

	private AdminUser createUser(String username, AdminRole role) {
		AdminUser user = new AdminUser(username, username + "@example.com", username,
				this.passwordEncoder.encode("Test-Only-Password9"), role, false, Instant.now());
		return this.adminUserRepository.saveAndFlush(user);
	}
}
