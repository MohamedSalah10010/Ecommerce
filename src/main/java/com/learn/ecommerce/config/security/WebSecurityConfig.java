package com.learn.ecommerce.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {

    private JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)  throws Exception {
            http
                .formLogin(form->form.disable())
                .httpBasic(basic->basic.disable())
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authRequest->
                        authRequest.requestMatchers(
                                 "/auth/login"
                                ,"/auth/verify"
                                ,"/auth/reset-password"
                                ,"/auth/forgot-password"
                                ,"/auth/request-verify"
                                ,"/products"
                                ,"/swagger-ui/**"
                                ,"/v3/api-docs/**")


                                .permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);




        return http.build();

    }

}
