package com.example.petitions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/auth/**", "/login", "/register",
                                "/petitions", "/petitions/**",
                                "/vote/**", "/vote/token/**",
                                "/css/**", "/images/**", "/js/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            String redirectTo = request.getParameter("redirectTo");
                            String contextPath = request.getContextPath(); // важливо для WAR-деплой

                            if (redirectTo != null && !redirectTo.isBlank()) {
                                response.sendRedirect(contextPath + redirectTo);
                            } else {
                                response.sendRedirect(contextPath + "/petitions");
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
