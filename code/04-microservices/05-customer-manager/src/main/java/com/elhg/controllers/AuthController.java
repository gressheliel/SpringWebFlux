package com.elhg.controllers;


import com.elhg.dtos.LoginRequest;
import com.elhg.dtos.LoginResponse;
import com.elhg.security.AuthService;
import com.elhg.services.CustomerService;
import com.elhg.tables.CustomerTable;
import com.elhg.tables.RoleTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path ="auth")
public class AuthController {

    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;


    @PostMapping(path = "login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest loginRequest){
        log.info("AuthController Attempting login for user: {}", loginRequest.getEmail());

        return authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword())
                .flatMap( jwt ->
                    customerService.readRolesByEmail(loginRequest.getEmail())
                            .map(rolesMap -> {
                                List<String> rolesName = rolesMap.values().stream()
                                        .flatMap(Collection::stream)
                                        .map(RoleTable::getName)
                                        .toList();
                                LoginResponse response = new LoginResponse(jwt, loginRequest.getEmail(), rolesName);
                                return ResponseEntity.ok(response);
                            })
                )
                .onErrorResume(throwable -> {
                    log.error("Login failed for user {}: {}", loginRequest.getEmail(), throwable.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }


    @PostMapping(path = "register")
    public Mono<ResponseEntity<CustomerTable>> createCustomer(@RequestBody CustomerTable customer,
                                                              @RequestParam Set<String> roles) {
        log.info("AuthController POST auth/register");

        customer.setUserPassword(passwordEncoder.encode(customer.getUserPassword()));

        return customerService.createCustomer(customer, roles)
                .map(createdCustomer -> ResponseEntity
                        .created(URI.create("auth/register/" + createdCustomer.getId()))
                        .body(createdCustomer))
                .onErrorResume(IllegalArgumentException.class, error -> {
                    log.error("POST  auth/register/ failed", error);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("POST  auth/register/ failed", error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
