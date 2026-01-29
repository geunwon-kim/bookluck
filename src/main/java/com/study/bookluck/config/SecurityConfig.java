package com.study.bookluck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Environment environment;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 보안 활성화 여부 확인 (환경 변수로 제어 가능)
        String securityEnabled = environment.getProperty("SECURITY_ENABLED", "false");
        boolean isSecurityEnabled = Boolean.parseBoolean(securityEnabled);
        
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
        if (isSecurityEnabled) {
            // 보안 활성화: 인증 필요
            http.authorizeHttpRequests(auth -> auth
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
                    "/.well-known/**",
                    "/error"
                ).permitAll()
                // 사용자 데이터 관련 API는 인증 필요
                .requestMatchers(
                    "/users/**",
                    "/books/record",
                    "/books/addToFavorites",
                    "/books/reading/**"
                ).authenticated()
                .anyRequest().authenticated()
            );
        } else {
            // 보안 비활성화: 모든 요청 허용 (테스트용)
            http.authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        }
        
        http.oauth2Login(oauth2 -> oauth2
            .defaultSuccessUrl("/login/success", true)
            .failureUrl("/login/failure")
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 프로덕션 환경 확인
        boolean isProduction = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        
        if (isProduction) {
            // 프로덕션: 실제 프론트엔드 도메인만 허용
            configuration.setAllowedOrigins(List.of(
                "https://bookluck.org",
                "https://www.bookluck.org"
            ));
        } else {
            // 개발 환경: 로컬 및 개발 서버 허용
            configuration.setAllowedOrigins(List.of(
                "http://front-server.com",
                "http://localhost:3000",
                "http://localhost:8080"
            ));
        }
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
