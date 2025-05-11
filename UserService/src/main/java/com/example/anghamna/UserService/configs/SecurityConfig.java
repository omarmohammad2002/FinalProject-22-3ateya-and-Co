package com.example.anghamna.UserService.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // correct way to disable CSRF in modern Spring Security
                .authorizeHttpRequests(auth -> auth
                        // public endpoints
                        .requestMatchers(
                                "/userapi/user/register",
                                "/userapi/user/createUser",
                                "/userapi/sessions/login"
                        ).permitAll()

                        // allow others only if authenticated
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
