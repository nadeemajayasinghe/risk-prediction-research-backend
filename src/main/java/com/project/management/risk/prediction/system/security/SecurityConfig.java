package com.project.management.risk.prediction.system.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Auth-ready Spring Security setup. JWT validation is enabled when
 * {@code security.jwt.enabled=true} (and an issuer-uri is supplied).
 * In dev profile JWT is off and all endpoints are open.
 */
@Configuration
public class SecurityConfig {

    @Value("${security.jwt.enabled:false}")
    private boolean jwtEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/swagger-ui/**", "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/actuator/**",
                            "/h2-console/**"
                    ).permitAll();
                    if (jwtEnabled) {
                        auth.anyRequest().authenticated();
                    } else {
                        auth.anyRequest().permitAll();
                    }
                })
                .headers(h -> h.frameOptions(f -> f.disable()));

        if (jwtEnabled) {
            http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        }
        return http.build();
    }
}
