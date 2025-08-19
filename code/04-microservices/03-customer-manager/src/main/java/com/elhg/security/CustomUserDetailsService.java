package com.elhg.security;

import com.elhg.repositories.CustomerRepository;
import com.elhg.services.CustomerService;
import com.elhg.tables.RoleTable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return customerRepository.findByEmail(email)
                .flatMap(customer ->
                        customerService.readRolesByEmail(email)
                                .map( roleMap -> {
                                    List<RoleTable> roles = roleMap.values().stream()
                                            .flatMap(Collection::stream)
                                            .toList();
                                    List<GrantedAuthority> authorities = roles.stream()
                                            .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getName())
                                            .toList();
                                    return User.builder()
                                            .username(customer.getEmail())
                                            .password(customer.getUserPassword())
                                            .authorities(authorities)
                                            .disabled(!customer.getEnabled())
                                            .accountExpired(!customer.getAccountNonExpired())
                                            .credentialsExpired(!customer.getCredentialsNonExpired())
                                            .accountLocked(Boolean.FALSE)
                                            .build();
                                })
                );

    }
}
