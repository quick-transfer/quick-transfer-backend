package com.weg.quicktransfer.config;

import com.weg.quicktransfer.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Marks this class as a source of bean definitions for the Spring context
@Configuration
// Enables Spring Security's web security support and MVC integration
@EnableWebSecurity
// Lombok annotation to automatically generate a constructor for final fields
@RequiredArgsConstructor
public class SecurityConfig {

    // Custom filter that intercepts requests to validate JWT tokens
    private final JwtAuthFilter jwtAuthFilter;

    // Creates the Spring Security Filter Chain for dictating which filters are applied
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())                       // Disables CSRF protection (safe for stateless JWT)
                .cors(Customizer.withDefaults())                                              // Enables CORS using the corsConfigurationSource bean
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))       // Configures session management to be stateless (no sessions)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()                // Allows public access to all authentication endpoints
                        .anyRequest().authenticated()                                         // Requires authentication for all other API endpoints
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)   // Executes the JWT filter before standard login authentication
                .build();
    }

    // Configures CORS settings to dictate which external domains can access the API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedHeaders(List.of("*"));                                           // Allows all HTTP headers in cross-origin requests
        config.setAllowCredentials(true);                                                     // Allows credentials (like auth headers) in cross-origin requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);                              // Applies this CORS configuration to all API endpoints

        return source;
    }

    // Exposes the AuthenticationManager to be used manually in the login controller
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Defines the PasswordEncoder used for hashing and verifying user passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();                                                   // Uses the strong bcrypt algorithm for password hashing
    }
}