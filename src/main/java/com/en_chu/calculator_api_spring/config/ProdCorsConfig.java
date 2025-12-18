package com.en_chu.calculator_api_spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Profile("prod") // 👈 只有生產環境生效
public class ProdCorsConfig {

	// 從 application-prod.yml 中讀取清單
	@Value("${app.cors.allowed-origins}")
	private List<String> allowedOrigins;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins(allowedOrigins.toArray(new String[0])) // 帶入白名單
						.allowedMethods("GET", "POST", "OPTIONS") // 正式環境建議縮減不必要的 Method
						.allowedHeaders("*").allowCredentials(true).maxAge(3600);
			}
		};
	}
}