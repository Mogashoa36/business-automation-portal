package company.backend.service;

import java.util.List;

import company.backend.dto.FaqRequest;
import company.backend.dto.FaqResponse;

/** Create, edit, publish, reorder and delete FAQ entries. */
public interface AdminFaqService {

	/** Every entry, published or not, in display order. */
	List<FaqResponse> findAll();

	FaqResponse create(String actor, FaqRequest request);

	FaqResponse update(String actor, Long id, FaqRequest request);

	void delete(String actor, Long id);
}