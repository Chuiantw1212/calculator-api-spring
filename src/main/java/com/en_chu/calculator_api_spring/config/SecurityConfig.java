package com.en_chu.calculator_api_spring.config;

import com.en_chu.calculator_api_spring.security.FirebaseAuthenticationEntryPoint;
import com.en_chu.calculator_api_spring.security.FirebaseTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseTokenFilter firebaseTokenFilter;
    private final FirebaseAuthenticationEntryPoint firebaseAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // ✅ 核心修正：優先放行所有的 OPTIONS 預檢請求
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public Endpoints
                .requestMatchers(
                    "/",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/metadata/**").permitAll()

                // Admin Endpoints (DEV ONLY)
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // User-specific Endpoints
                .requestMatchers(
                    "/api/v1/auth/sync",
                    "/api/v1/user/**"
                ).authenticated()

                // Default Rule: Deny all other requests
                .anyRequest().denyAll()
            )
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(e -> e.authenticationEntryPoint(firebaseAuthenticationEntryPoint));

        return http.build();
    }
}
