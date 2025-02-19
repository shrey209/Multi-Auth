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
            .csrf(csrf -> csrf.disable()) // Disable CSRF (optional)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register").permitAll() // Allow public access
                .requestMatchers("/auth/login").permitAll() // Allow login without authentication
                .requestMatchers("/auth/validate").permitAll()
                .anyRequest().authenticated() // Secure all other endpoints
            );

        return http.build();
    }
}
