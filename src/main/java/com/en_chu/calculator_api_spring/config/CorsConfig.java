package com.en_chu.calculator_api_spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // 允許所有路徑
						.allowedOrigins("http://localhost:5173") // 允許你的 Vue 3 來源
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的請求方法
						.allowedHeaders("*") // 允許所有的 Header
						.allowCredentials(true) // 是否允許攜帶 Cookie 或憑證
						.maxAge(3600); // 預檢請求 (Preflight) 的快取時間 (秒)
			}
		};
	}
}