package com.elhg.security;

import com.elhg.helpers.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtHelper jwtHelper;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String jwt = authentication.getCredentials().toString();

        if(!jwtHelper.validateJwt(jwt)) {
            return Mono.empty();
        }
        final String username = jwtHelper.getUsernameFromJwt(jwt);
        final List<String> roles = jwtHelper.getRolesFromJwt(jwt);

        final List<GrantedAuthority> authorities = roles.stream()
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role)
                .toList();
        // Create an Authentication object with the username, JWT, and authorities
        final Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

        return Mono.just(auth);
    }
}
