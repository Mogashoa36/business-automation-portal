package company.backend.service;

import java.util.List;

import company.backend.dto.SiteContentResponse;

/**
 * Editable, non sensitive website content such as page titles and meta
 * descriptions.
 *
 * <p>Only allow listed keys can be written, which is what keeps application
 * configuration (database credentials, signing keys, environment variables)
 * out of reach of the admin UI entirely.
 */
public interface SiteContentService {

	List<SiteContentResponse> findAll();

	SiteContentResponse update(String actor, String key, String value);
}