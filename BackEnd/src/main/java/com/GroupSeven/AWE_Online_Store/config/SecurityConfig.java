package com.GroupSeven.AWE_Online_Store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for testing APIs with Postman
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register").permitAll() // Allow register endpoint
                        .anyRequest().authenticated() // Other endpoints require login
                );

        return http.build();
    }
}
