package company.backend.dto;

import java.time.Instant;

/** Receipt returned to the sender after a contact message has been stored. */
public record ContactMessageResponse(String reference, String service, Instant receivedAt, String confirmation) {
}
