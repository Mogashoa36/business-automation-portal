package company.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import company.backend.domain.ServiceInterest;
import company.backend.entity.ContactMessage;
import company.backend.repo.ContactMessageRepository;

/**
 * End to end tests for the public contact endpoint: happy path, bean validation
 * and business rule enforcement.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContactControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ContactMessageRepository contactMessageRepository;

	@Test
	void storesValidEnquiryAndReturnsReceipt() throws Exception {
		String payload = """
				{
				  "fullName": "  Thandi Mokoena  ",
				  "company": "  Mokoena Logistics  ",
				  "email": "Thandi@Example.COM",
				  "phone": "+27 82 000 0000",
				  "service": "Business Process Automation",
				  "message": "We still approve supplier invoices by email and need this automated."
				}
				""";

		mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.reference", containsString("CM-")))
				.andExpect(jsonPath("$.service", is("Business Process Automation")))
				.andExpect(jsonPath("$.confirmation", containsString("received")));

		ContactMessage stored = this.contactMessageRepository.findAll().get(0);
		assertThat(stored.getFullName()).isEqualTo("Thandi Mokoena");
		assertThat(stored.getCompany()).isEqualTo("Mokoena Logistics");
		assertThat(stored.getEmail()).isEqualTo("thandi@example.com");
		assertThat(stored.getService()).isEqualTo(ServiceInterest.BUSINESS_PROCESS_AUTOMATION);
		assertThat(stored.getReference()).startsWith("CM-");
	}

	@Test
	void acceptsEveryPublishedServiceOption() throws Exception {
		for (ServiceInterest interest : ServiceInterest.values()) {
			mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
					.content(payload("Jane Doe", interest.name(), "We would like to discuss this service in more detail.")))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.service", is(interest.getDisplayName())));
		}
	}

	@Test
	void rejectsMissingRequiredFields() throws Exception {
		String payload = """
				{ "fullName": "", "email": "not-an-email", "service": "", "message": "short" }
				""";

		mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(payload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Please correct the highlighted fields and try again.")))
				.andExpect(jsonPath("$.fieldErrors", hasKey("fullName")))
				.andExpect(jsonPath("$.fieldErrors", hasKey("email")))
				.andExpect(jsonPath("$.fieldErrors", hasKey("service")))
				.andExpect(jsonPath("$.fieldErrors", hasKey("message")));

		assertThat(this.contactMessageRepository.count()).isZero();
	}

	@Test
	void rejectsOversizedMessage() throws Exception {
		mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
				.content(payload("Jane Doe", "Website Development", "x".repeat(4001))))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors", hasKey("message")));
	}

	@Test
	void rejectsUnknownService() throws Exception {
		mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
				.content(payload("Jane Doe", "Astrology Consulting", "Please build me something magical.")))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors", hasKey("service")))
				.andExpect(jsonPath("$.fieldErrors.service", containsString("Business Process Automation")));

		assertThat(this.contactMessageRepository.count()).isZero();
	}

	@Test
	void rejectsUnreadableBody() throws Exception {
		mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content("{ not json"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.status", is(400)))
				.andExpect(jsonPath("$.message", containsString("could not be read")));
	}

	/** Builds a valid contact request body, overriding the pieces a test needs. */
	private String payload(String fullName, String service, String message) {
		return """
				{
				  "fullName": "%s",
				  "company": "Mokoena Logistics",
				  "email": "jane@example.com",
				  "phone": "+27 82 000 0000",
				  "service": "%s",
				  "message": "%s"
				}
				""".formatted(fullName, service, message);
	}
}
