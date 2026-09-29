package company.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.dto.FaqResponse;
import company.backend.repo.FaqItemRepository;

@Service
@Transactional(readOnly = true)
public class FaqServiceImpl implements FaqService {

	private final FaqItemRepository faqItemRepository;

	public FaqServiceImpl(FaqItemRepository faqItemRepository) {
		this.faqItemRepository = faqItemRepository;
	}

	@Override
	public List<FaqResponse> findPublished() {
		return this.faqItemRepository.findByPublishedTrueOrderByDisplayOrderAsc().stream()
			.map(FaqResponse::from)
			.toList();
	}
}