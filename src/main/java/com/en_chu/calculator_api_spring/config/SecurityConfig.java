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
            // 1. CORS and CSRF Configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Session Management: Stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. Authorization Rules
            .authorizeHttpRequests(authz -> authz
                // 3.1 Public Endpoints
                .requestMatchers(
                    "/",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/metadata/**").permitAll()

                // 3.2 Admin Endpoints (DEV ONLY)
                // For safety, these endpoints are completely disabled in non-dev profiles.
                // The @Profile("dev") on the AdminController already handles this.
                // We add a rule here for clarity and defense-in-depth.
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // Assuming an ADMIN role for future use

                // 3.3 User-specific Endpoints
                .requestMatchers(
                    "/api/v1/auth/sync",
                    "/api/v1/user/**"
                ).authenticated()

                // 3.4 Default Rule: Deny all other requests
                .anyRequest().denyAll()
            )

            // 4. Custom Firebase Filter
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)

            // 5. Exception Handling
            .exceptionHandling(e -> e.authenticationEntryPoint(firebaseAuthenticationEntryPoint));

        return http.build();
    }
}
