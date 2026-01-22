package com.study.bookluck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // 공개 API (인증 불필요)
                .requestMatchers(
                    "/", 
                    "/login/oauth2/**", 
                    "/oauth2/**", 
                    "/login/**",
                    "/books/getAllBooks",
                    "/books/getApiBooks",
                    "/books-post/getAllBooks",
                    "/phrases/**",
                    "/actuator/**",
                    "/error",
                    "/.well-known/**"  // Let's Encrypt certbot 챌린지 경로
                ).permitAll()
                // 사용자 데이터 관련 API는 인증 필요
                .requestMatchers(
                    "/users/**",
                    "/books/record",
                    "/books/addToFavorites",
                    "/books/reading/**"
                ).authenticated()
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/login/success", true)
                .failureUrl("/login/failure")
            )
            // OAuth2 로그인을 명시적으로만 사용 (자동 리다이렉트 방지)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    // 인증이 필요한 API에만 401 반환 (자동 리다이렉트 방지)
                    if (request.getRequestURI().startsWith("/api/") || 
                        request.getRequestURI().startsWith("/users/") ||
                        request.getRequestURI().startsWith("/books/record") ||
                        request.getRequestURI().startsWith("/books/addToFavorites") ||
                        request.getRequestURI().startsWith("/books/reading/")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Unauthorized\"}");
                    } else {
                        // 그 외의 경우에만 로그인 페이지로 리다이렉트
                        response.sendRedirect("/login/google");
                    }
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://front-server.com", "http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
