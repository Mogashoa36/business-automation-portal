package company.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordPolicyTest {

	@Test
	void acceptsAStrongPassword() {
		assertThat(PasswordPolicy.validate("Correct-Horse9")).isNull();
	}

	@Test
	void rejectsAPasswordThatIsTooShort() {
		assertThat(PasswordPolicy.validate("Short1A")).contains("at least 12");
	}

	@Test
	void rejectsAPasswordWithNoUppercaseLetter() {
		assertThat(PasswordPolicy.validate("correcthorse9")).contains("uppercase");
	}

	@Test
	void rejectsAPasswordWithNoLowercaseLetter() {
		assertThat(PasswordPolicy.validate("CORRECTHORSE9")).contains("lowercase");
	}

	@Test
	void rejectsAPasswordWithNoNumber() {
		assertThat(PasswordPolicy.validate("Correct-Horse")).contains("number");
	}

	@Test
	void rejectsAnEmptyPassword() {
		assertThat(PasswordPolicy.validate("")).isNotNull();
		assertThat(PasswordPolicy.validate(null)).isNotNull();
	}

	/**
	 * BCrypt only hashes the first 72 bytes, so an unbounded password would
	 * make the stored hash and the supplied value describe different secrets.
	 */
	@Test
	void rejectsAPasswordLongerThanBcryptConsumes() {
		assertThat(PasswordPolicy.validate("A1a".repeat(40))).contains("100 characters or fewer");
	}
}