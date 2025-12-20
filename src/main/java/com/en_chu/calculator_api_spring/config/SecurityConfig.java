package com.en_chu.calculator_api_spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.en_chu.calculator_api_spring.security.FirebaseTokenFilter;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 1. 關閉 CSRF (REST API 不需要)
				.csrf(AbstractHttpConfigurer::disable)

				// 2. ❌ 徹底關閉瀏覽器預設的登入彈窗 (Basic Auth & Form Login)
				// 這樣 Swagger 或 Postman 測試時，才不會一直跳出帳號密碼框
				.httpBasic(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)

				// 3. 設定路徑權限 (白名單 vs 黑名單)
				.authorizeHttpRequests(auth -> auth
						// ✅ Swagger UI 必備的白名單 (這些路徑必須公開)
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/v1/metadata")
						.permitAll()

						// ✅ 假設你有公開的 API (例如計算機功能不想鎖)，也可以加在這裡
						// .requestMatchers("/api/v1/calculator/**").permitAll()

						// 🔒 其他所有 /api/** 開頭的請求，都必須要有 Token 才能過
						.anyRequest().authenticated())

				// 4. 插入我們寫好的 Firebase 過濾器
				.addFilterBefore(new FirebaseTokenFilter(), UsernamePasswordAuthenticationFilter.class)

				// 5. 例外處理：如果沒登入硬闖，直接回傳 401 (不要 redirect 到登入頁)
				.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

		return http.build();
	}
}