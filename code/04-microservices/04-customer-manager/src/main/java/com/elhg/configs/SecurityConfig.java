package com.elhg.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String LOGIN_ENDPOINT = "/auth/login";
    private static final String REGISTER_ENDPOINT = "/auth/register";
    private static final String MANAGER_PATH = "/manager/**";
    private static final String RATING_PATH = "/rating/**";
    private static final String RESERVATION_PATH = "/reservation/**";

    private static final String ROLE_ADMIN = "admin_user";
    private static final String ROLE_PREMIUM = "premium_user";
    private static final String ROLE_FREE = "free_user";

    private final ReactiveAuthenticationManager authenticationManager;
    private final ServerAuthenticationConverter converter;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain (ServerHttpSecurity http){
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationFilter.setServerAuthenticationConverter(converter);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange->
                        exchange
                                .pathMatchers(HttpMethod.POST, LOGIN_ENDPOINT, REGISTER_ENDPOINT).permitAll()
                                .pathMatchers(MANAGER_PATH).hasRole(ROLE_ADMIN)
                                .pathMatchers(RATING_PATH).hasAnyRole(ROLE_PREMIUM)
                                .pathMatchers(RESERVATION_PATH).hasAnyRole(ROLE_PREMIUM)
                                .anyExchange().authenticated()
                )
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
