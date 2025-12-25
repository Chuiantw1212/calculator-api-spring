package com.en_chu.calculator_api_spring.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 1. 允許的前端來源
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));

		// 2. 允許的方法
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// 3. 允許的標頭
		configuration.setAllowedHeaders(List.of("*"));

		// 4. 允許攜帶憑證
		configuration.setAllowCredentials(true);

		// 5. Preflight 快取時間
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}