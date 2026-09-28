package company.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web layer configuration.
 *
 * <p>Only the JSON API is opened up to the configured origins; the Angular
 * application itself is served separately.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final CorsProperties corsProperties;

	public WebConfig(CorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		if (corsProperties.allowedOrigins().isEmpty()) {
			return;
		}
		registry.addMapping("/api/**").allowedOrigins(corsProperties.allowedOrigins().toArray(String[]::new))
				.allowedMethods("GET", "POST", "OPTIONS").allowedHeaders(HttpHeaders.ACCEPT, HttpHeaders.CONTENT_TYPE)
				.allowCredentials(false).maxAge(3600);
	}
}
