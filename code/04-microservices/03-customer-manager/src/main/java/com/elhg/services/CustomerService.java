package com.elhg.services;

import com.elhg.enums.UpdateRoleOperation;
import com.elhg.tables.CustomerTable;
import com.elhg.tables.RoleTable;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

public interface CustomerService {
    Mono<CustomerTable> createCustomer(CustomerTable customer, Set<String> roleNames);
    Mono<Map<String, Set<RoleTable>>> readRolesByEmail(String email);
    Mono<Void> deleteCustomer(Long id);
    Mono<CustomerTable> updateRoleInCustomer(Long id,
                                             Set<String> roleTables,
                                             UpdateRoleOperation operation);
}
