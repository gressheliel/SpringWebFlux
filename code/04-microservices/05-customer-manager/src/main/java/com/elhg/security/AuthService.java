package com.elhg.security;


import com.elhg.helpers.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;


    public Mono<String> authenticate(String email, String password) {
        return customUserDetailsService.findByUsername(email)
                .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword()))
                .map(userDetails -> {
                    List<String> authorities = userDetails.getAuthorities().stream()
                            .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                            .toList();
                    return jwtHelper.generateJwt(email, authorities);
                }).switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }



}
