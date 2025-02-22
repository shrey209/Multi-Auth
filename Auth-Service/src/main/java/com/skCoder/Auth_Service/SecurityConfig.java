package com.skCoder.Auth_Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for testing (enable in production)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/github/login",  // Returns GitHub OAuth URL
                    "/auth/github/callback", // Handles GitHub OAuth callback (code exchange)
                    "/authv1/register", "/authv1/login", "/authv1/validate" // Standard auth endpoints
                    ,"/auth/google/callback","/auth/google/login"
                ).permitAll() // Allow public access to these endpoints
                .anyRequest().authenticated() // Secure all other requests
            );

        return http.build();
    }
}
