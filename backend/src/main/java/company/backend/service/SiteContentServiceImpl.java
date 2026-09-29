package company.backend.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.config.EditableContentKeys;
import company.backend.dto.SiteContentResponse;
import company.backend.entity.SiteContent;
import company.backend.exception.InvalidRequestException;
import company.backend.repo.SiteContentRepository;

/**
 * Editable website content.
 *
 * <p>Writes are constrained to {@link EditableContentKeys}, so the admin UI
 * can edit presentation copy but never application configuration. Values are
 * plain text rendered by Angular interpolation, so there is no stored HTML to
 * sanitise.
 *
 * <p>The frontend keeps its compiled in defaults, which is why a key with no
 * row simply means "use the built in value". A blank value stored here is
 * treated as absent for the same reason, so a page can never be left without a
 * title.
 */
@Service
public class SiteContentServiceImpl implements SiteContentService {

	private static final int MAX_VALUE_LENGTH = 2000;

	private final SiteContentRepository siteContentRepository;

	public SiteContentServiceImpl(SiteContentRepository siteContentRepository) {
		this.siteContentRepository = siteContentRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SiteContentResponse> findAll() {
		return this.siteContentRepository.findAll().stream().map(SiteContentServiceImpl::toResponse).toList();
	}

	@Override
	@Transactional
	public SiteContentResponse update(String actor, String key, String value) {
		if (!EditableContentKeys.isAllowed(key)) {
			// This is the guard that keeps sensitive configuration uneditable.
			throw new InvalidRequestException("That content key cannot be edited from the administration portal.");
		}

		String trimmed = value == null ? null : value.trim();
		if (trimmed != null && trimmed.length() > MAX_VALUE_LENGTH) {
			throw new InvalidRequestException("Content must be " + MAX_VALUE_LENGTH + " characters or fewer.");
		}

		SiteContent content = this.siteContentRepository.findByContentKey(key)
			.orElseGet(() -> new SiteContent(key, trimmed, actor, Instant.now()));
		content.update(trimmed, actor, Instant.now());

		return toResponse(this.siteContentRepository.save(content));
	}

	private static SiteContentResponse toResponse(SiteContent content) {
		return new SiteContentResponse(content.getContentKey(), content.getContentValue(), content.getUpdatedBy(),
				content.getUpdatedAt());
	}
}