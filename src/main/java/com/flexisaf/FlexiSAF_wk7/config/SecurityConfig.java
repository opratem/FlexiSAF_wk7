package com.flexisaf.FlexiSAF_wk7.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!test")
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Disable CSRF for testing REST APIs
                .csrf (csrf -> csrf.disable())

                //Authorize requests
                .authorizeHttpRequests(auth -> auth
                        //Allow Swagger UI and API docs to be accessible
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        //Permit all access to /api/** endpoints for now
                        //(You can restrict later to authenticated users)
                        .requestMatchers("/api/**").permitAll()

                        //Require authentication for any other request
                        .anyRequest().authenticated()
                )

                //Optional: Disable default login form (not needed for APIs)
                .httpBasic(httpBasic -> {})
                .formLogin(form -> form.disable());

        return http.build();
    }
}
