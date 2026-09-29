package company.backend.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * A page of admin table rows.
 *
 * <p>Total count and total pages are computed server side so the client never
 * has to download a full dataset just to render a pager.
 */
public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages, boolean first,
		boolean last) {

	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages(), page.isFirst(), page.isLast());
	}
}