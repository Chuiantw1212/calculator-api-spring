package com.en_chu.calculator_api_spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

// 1. 記得 import 我們剛剛寫的 EntryPoint
import com.en_chu.calculator_api_spring.security.FirebaseAuthenticationEntryPoint;
import com.en_chu.calculator_api_spring.security.FirebaseTokenFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	private FirebaseTokenFilter firebaseTokenFilter;

	// 2. 注入自定義的 EntryPoint
	@Autowired
	private FirebaseAuthenticationEntryPoint firebaseAuthenticationEntryPoint;

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource)).csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						auth -> auth
								.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
										"/api/v1/metadata", "/admin/sync-firebase")
								.permitAll().anyRequest().authenticated())
				.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)

				// 3. 修改這裡：換成我們自定義的 EntryPoint
				// 原本是: .authenticationEntryPoint(new
				// HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.exceptionHandling(e -> e.authenticationEntryPoint(firebaseAuthenticationEntryPoint));

		return http.build();
	}
}