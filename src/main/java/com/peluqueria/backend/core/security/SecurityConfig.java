package com.peluqueria.backend.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final List<String> allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${app.cors.allowed-origins:http://localhost:4200}") String allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        List<String> origins = new java.util.ArrayList<>(List.of(
                "http://localhost:4200",
                "http://localhost:8080",
                "https://peluqueria.joserodriguezdeveloper.com"
        ));
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .filter(origin -> !origin.isEmpty() && !origins.contains(origin))
                    .forEach(origins::add);
        }
        this.allowedOrigins = origins;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        // Public auth paths
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Public initial setup paths
                        .requestMatchers("/api/v1/setup/**").permitAll()
                        // Public endpoints for decoupled OTP reservations
                        .requestMatchers("/api/public/**").permitAll()
                        // Public H2 Console
                        .requestMatchers("/h2-console/**").permitAll()
                        // CORS preflight requests must not require a JWT.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public read-only access to blog and catalog
                        .requestMatchers(HttpMethod.GET, "/api/v1/blog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        // Media resources are public; library management requires an ADMIN JWT.
                        .requestMatchers(HttpMethod.GET, "/api/media", "/api/media/**", "/api/v1/media", "/api/v1/media/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/media", "/api/media/**", "/api/v1/media", "/api/v1/media/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/media", "/api/media/**", "/api/v1/media", "/api/v1/media/**").hasRole("ADMIN")
                        // Public read-only access to services, workers, and available slots
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/workers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointments/available").permitAll()
                        // Require ADMIN role for admin endpoints
                        .requestMatchers("/api/admin/**", "/api/v1/admin/**").hasRole("ADMIN")
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Cache-Control", "*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "Location"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
