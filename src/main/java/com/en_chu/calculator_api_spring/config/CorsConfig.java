package com.en_chu.calculator_api_spring.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "app.cors") // ✅ 1. 指定 YAML 前綴
public class CorsConfig {

	// ✅ 2. 定義變數 (名稱要跟 YAML 的 allowed-origins 對應，駝峰式命名)
	// 給一個預設空 List，避免 NullPointerException
	private List<String> allowedOrigins = new ArrayList<>();

	// ✅ 3. 重要！必須要有 Setter 才能注入值 (如果您沒有用 Lombok，請手動生成 setAllowedOrigins 方法)
	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 使用注入進來的 List
		configuration.setAllowedOrigins(allowedOrigins);

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@PostConstruct
    public void init() {
        System.out.println("🔥 [CorsConfig] 目前允許的 Origins: " + allowedOrigins);
    }
}