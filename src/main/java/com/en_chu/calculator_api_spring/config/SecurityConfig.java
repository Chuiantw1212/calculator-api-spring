package com.en_chu.calculator_api_spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.en_chu.calculator_api_spring.security.FirebaseAuthenticationEntryPoint;
import com.en_chu.calculator_api_spring.security.FirebaseTokenFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	private FirebaseTokenFilter firebaseTokenFilter;

	@Autowired
	private FirebaseAuthenticationEntryPoint firebaseAuthenticationEntryPoint;

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource)).csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/v1/metadata", "/api/tools/**",

								// ✅ [修改] 更新為新的 Admin Controller 路徑 (使用萬用字元)
								"/admin/sync/**")
						.permitAll().anyRequest().authenticated())
				.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(e -> e.authenticationEntryPoint(firebaseAuthenticationEntryPoint));

		return http.build();
	}
}