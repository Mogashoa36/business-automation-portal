package company.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import company.backend.config.ContactProperties;
import company.backend.domain.ServiceInterest;
import company.backend.dto.ContactMessageRequest;
import company.backend.dto.ContactMessageResponse;
import company.backend.entity.ContactMessage;
import company.backend.exception.ContactSubmissionRateLimitException;
import company.backend.exception.InvalidServiceInterestException;
import company.backend.repo.ContactMessageRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContactMessageServiceImplTest {

	@Mock
	private ContactMessageRepository contactMessageRepository;

	private ContactMessageServiceImpl service;

	@BeforeEach
	void setUp() {
		this.service = new ContactMessageServiceImpl(this.contactMessageRepository,
				new ContactProperties(5, Duration.ofMinutes(10)));
		when(this.contactMessageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
	}

	@Test
	void trimsAndNormalisesTheStoredEnquiry() {
		ContactMessageResponse response = this.service
				.submit(request("  Lindiwe  ", "  Acme  ", "  LINDIwe@Example.com ", " Reporting & Dashboards ", "  We need help.  "),
						"client-1");

		ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
		verify(this.contactMessageRepository).save(captor.capture());
		ContactMessage stored = captor.getValue();

		assertThat(stored.getFullName()).isEqualTo("Lindiwe");
		assertThat(stored.getCompany()).isEqualTo("Acme");
		assertThat(stored.getEmail()).isEqualTo("lindiwe@example.com");
		assertThat(stored.getMessage()).isEqualTo("We need help.");
		assertThat(stored.getService()).isEqualTo(ServiceInterest.REPORTING_DASHBOARDS);
		assertThat(stored.getReference()).matches("^CM-\\d{8}-[0-9A-Z]{4}$");
		assertThat(response.reference()).isEqualTo(stored.getReference());
		assertThat(response.service()).isEqualTo("Reporting & Dashboards");
		assertThat(response.receivedAt()).isNotNull();
	}

	@Test
	void storesEmptyOptionalFieldsAsNull() {
		this.service.submit(new ContactMessageRequest("Lindiwe", "  ", "lindiwe@example.com", null, "Other", "Please call me."),
				"client-2");

		ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
		verify(this.contactMessageRepository).save(captor.capture());
		assertThat(captor.getValue().getCompany()).isNull();
		assertThat(captor.getValue().getPhone()).isNull();
	}

	@Test
	void rejectsUnknownServiceWithoutWriting() {
		assertThatThrownBy(() -> this.service.submit(request("Lindiwe", "Acme", "lindiwe@example.com", "Fortune Telling",
				"Please help."), "client-3"))
				.isInstanceOf(InvalidServiceInterestException.class).hasMessageContaining("Business Process Automation");

		verify(this.contactMessageRepository, never()).save(any());
	}

	@Test
	void acceptsTheEnumConstantNameAsWellAsTheDisplayName() {
		ContactMessageResponse response = this.service.submit(
				request("Lindiwe", "Acme", "lindiwe@example.com", "PROCUREMENT", "Please help."), "client-4");
		assertThat(response.service()).isEqualTo("Procurement");
	}

	@Test
	void blocksClientsThatExceedTheSubmissionLimit() {
		ContactMessageServiceImpl limited = new ContactMessageServiceImpl(this.contactMessageRepository,
				new ContactProperties(2, Duration.ofMinutes(10)));

		limited.submit(request("A", "Acme", "a@example.com", "Other", "First message."), "noisy-client");
		limited.submit(request("B", "Acme", "b@example.com", "Other", "Second message."), "noisy-client");

		assertThatThrownBy(() -> limited.submit(request("C", "Acme", "c@example.com", "Other", "Third message."),
				"noisy-client")).isInstanceOf(ContactSubmissionRateLimitException.class)
				.hasMessageContaining("Too many enquiries");

		// A different client is unaffected by another client's activity.
		limited.submit(request("D", "Acme", "d@example.com", "Other", "Fourth message."), "quiet-client");
	}

	private ContactMessageRequest request(String fullName, String company, String email, String service, String message) {
		return new ContactMessageRequest(fullName, company, email, null, service, message);
	}
}
