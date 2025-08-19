package com.elhg.controllers;


import com.elhg.enums.UpdateRoleOperation;
import com.elhg.services.CustomerService;
import com.elhg.tables.CustomerTable;
import com.elhg.tables.RoleTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/manager")
public class ManagerController {

    private final CustomerService customerService;


    @GetMapping("/roles/{email}")
    public Mono<ResponseEntity<Map<String, Set<RoleTable>>>> getRolesByEmail(@PathVariable String email) {
        log.info("GET customers/manager/roles/{}", email);

        return customerService.readRolesByEmail(email)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, error -> {
                    log.error("GET customers/manager/roles/{} failed", email, error);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("GET customers/manager/roles/{} failed", email, error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PutMapping("/{id}/roles")
    public Mono<ResponseEntity<CustomerTable>> updateRoles(@PathVariable Long id,
                                                           @RequestParam Set<String> roles,
                                                           @RequestParam UpdateRoleOperation operation) {
        log.info("PUT customers/manager/{}/roles", id);

        return customerService.updateRoleInCustomer(id, roles, operation)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, error -> {
                    log.error("PUT customers/manager/{}/roles failed", id, error);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("PUT customers/manager/{}/roles failed", id, error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteCustomer(@PathVariable Long id) {
        log.info("DELETE customers/manager/{}", id);

        return customerService.deleteCustomer(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(IllegalArgumentException.class, error -> {
                    log.error("DELETE customers/manager/{} failed", id, error);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("DELETE customers/manager/{} failed", id, error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
