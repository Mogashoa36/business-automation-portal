package company.backend.config;

import java.util.List;
import java.util.Set;

/**
 * The complete list of content keys an administrator is allowed to edit.
 *
 * <p>This allow list is the security control that keeps application
 * configuration out of the CMS. A key that is not listed here is rejected by
 * the service, so there is no way to reach a database credential, a signing
 * key or an environment variable through the admin UI, no matter what the
 * client sends.
 */
public final class EditableContentKeys {

	/** {@code seo:<page>} values: a page title and meta description. */
	public static final Set<String> SEO_KEYS = Set.of("seo:home:title", "seo:home:description");

	/**
	 * Editable company presentation values. Contact email and phone are
	 * intentionally absent: no address has been supplied, and inventing one
	 * would be worse than omitting it.
	 */
	public static final Set<String> SETTING_KEYS = Set.of("setting:companyName", "setting:tagline",
			"setting:positioning", "setting:businessHours", "setting:location", "setting:primaryCtaLabel",
			"setting:primaryCtaLink");

	private EditableContentKeys() {
		// Constant holder.
	}

	/** @return true when the key may be written through the admin UI */
	public static boolean isAllowed(String key) {
		return key != null && (SEO_KEYS.contains(key) || SETTING_KEYS.contains(key));
	}

	/** @return every allowed key, for the admin settings screen */
	public static List<String> all() {
		return java.util.stream.Stream.concat(SEO_KEYS.stream(), SETTING_KEYS.stream()).sorted().toList();
	}
}