package com.en_chu.calculator_api_spring.config;

import org.springframework.beans.factory.annotation.Autowired;
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

	// 1. 注入你的 Filter (前提是 FirebaseTokenFilter 有加 @Component)
	@Autowired
	private FirebaseTokenFilter firebaseTokenFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/v1/metadata")
						.permitAll().anyRequest().authenticated())
				// 2. 這裡直接使用注入的 instance，不要 new
				.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)

				.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

		return http.build();
	}
}