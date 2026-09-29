package company.backend.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Authorization roles for the administration area.
 *
 * <p>Only the minimum that is useful is modelled. {@code ADMIN} can manage
 * content, enquiries, site settings and other administrator accounts.
 * {@code EDITOR} can manage website content and enquiries, but cannot change
 * accounts or system settings. That keeps day to day publishing separate from
 * account administration without adding a larger role model that nothing would
 * use.
 *
 * <p>Every protected endpoint verifies a role on the server. The Angular UI
 * hides controls an editor cannot use, but that is presentation only and is
 * never treated as a security boundary.
 */
public enum AdminRole {

	/** Full access, including administrator accounts and site settings. */
	ADMIN("Administrator"),

	/** Content and enquiry management only. */
	EDITOR("Editor");

	private final String displayName;

	AdminRole(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return this.displayName;
	}
}