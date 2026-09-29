package company.backend.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.dto.FaqRequest;
import company.backend.dto.FaqResponse;
import company.backend.entity.FaqItem;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.FaqItemRepository;

/**
 * FAQ content management.
 *
 * <p>Answers are stored and returned as plain text. The admin UI uses a
 * textarea and the public template renders the value with interpolation, so
 * there is no HTML to sanitise and no injection surface.
 */
@Service
public class AdminFaqServiceImpl implements AdminFaqService {

	private final FaqItemRepository faqItemRepository;

	public AdminFaqServiceImpl(FaqItemRepository faqItemRepository) {
		this.faqItemRepository = faqItemRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FaqResponse> findAll() {
		return this.faqItemRepository.findAll().stream().sorted(java.util.Comparator.comparingInt(FaqItem::getDisplayOrder))
			.map(FaqResponse::from)
			.toList();
	}

	@Override
	@Transactional
	public FaqResponse create(String actor, FaqRequest request) {
		Instant now = Instant.now();
		int order = request.displayOrder() == null ? (int) this.faqItemRepository.count() + 1
				: request.displayOrder();
		FaqItem item = new FaqItem(request.question().trim(), request.answer().trim(), trimToNull(request.category()),
				request.published(), order, now);
		return FaqResponse.from(this.faqItemRepository.save(item));
	}

	@Override
	@Transactional
	public FaqResponse update(String actor, Long id, FaqRequest request) {
		FaqItem item = this.faqItemRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No FAQ found with id " + id + "."));

		item.setQuestion(request.question().trim());
		item.setAnswer(request.answer().trim());
		item.setCategory(trimToNull(request.category()));
		item.setPublished(request.published());
		if (request.displayOrder() != null) {
			item.setDisplayOrder(request.displayOrder());
		}
		item.touch(Instant.now());

		return FaqResponse.from(this.faqItemRepository.save(item));
	}

	@Override
	@Transactional
	public void delete(String actor, Long id) {
		this.faqItemRepository.delete(
				this.faqItemRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("No FAQ found with id " + id + ".")));
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}