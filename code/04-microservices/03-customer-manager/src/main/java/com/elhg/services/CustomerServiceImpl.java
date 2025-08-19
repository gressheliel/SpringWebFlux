package com.elhg.services;

import com.elhg.enums.UpdateRoleOperation;
import com.elhg.repositories.CustomerRepository;
import com.elhg.repositories.RoleRepository;
import com.elhg.tables.CustomerTable;
import com.elhg.tables.RoleTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements  CustomerService{

    private final CustomerRepository customerRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<CustomerTable> createCustomer(CustomerTable customer, Set<String> roleNames) {
        log.info("Creating customer with email: {}", customer.getEmail());
        return customerRepository.save(customer)
                .flatMap(savedCustomer -> {
                    return Flux.fromIterable(roleNames)
                            .flatMap(role -> {
                                log.info("Assigning role: {} to customer: {}", role, savedCustomer.getEmail());
                                return databaseClient.sql(INSERT_ROLE_QUERY)
                                        .bind("customerId", savedCustomer.getId())
                                        .bind("roleName", role)
                                        .fetch()
                                        .rowsUpdated();
                            })
                            .then(Mono.just(savedCustomer));
                })
                .doOnSuccess(savedCustomer -> log.info("Customer created successfully: {}", savedCustomer.getEmail()))
                .doOnError(error -> log.error("Error creating customer: {}", error.getMessage()));
    }

    @Override
    public Mono<Map<String, Set<RoleTable>>> readRolesByEmail(String email) {
        log.info("Reading roles for customer with email: {}", email);
        return customerRepository.findByEmail(email)
                .flatMap(customer -> databaseClient.sql(SELECT_BY_ROLE_QUERY)
                        .bind("customerId", customer.getId())
                        .map((row, metadata) -> new RoleTable(
                                row.get("name", String.class),
                                row.get("description", String.class)
                        ))
                        .all()
                        .collectList()
                        .map(roles -> Map.of(customer.getEmail(), Set.copyOf(roles)))
                )
                .doOnSuccess(rolesMap -> log.info("Roles retrieved successfully for email: {}", email))
                .doOnError(error -> log.error("Error reading roles for email {}: {}", email, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);
        return customerRepository.deleteById(id)
                .doOnSuccess(unused -> log.info("Customer deleted successfully with id: {}", id))
                .doOnError(error -> log.error("Error deleting customer with id {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<CustomerTable> updateRoleInCustomer(Long id, Set<String> roleTables, UpdateRoleOperation operation) {
        log.info("Updating roles for customer with id: {}. Operation: {}", id, operation);
        return customerRepository.findById(id)
                .flatMap(customerDB -> {
                    if(operation == UpdateRoleOperation.ADD) {
                        return Flux.fromIterable(roleTables)
                                .flatMap(roleName -> {
                                    log.info("Adding role: {} to customer: {}", roleName, customerDB.getEmail());
                                    return databaseClient.sql(INSERT_ROLE_QUERY)
                                            .bind("customerId", customerDB.getId())
                                            .bind("roleName", roleName)
                                            .fetch()
                                            .rowsUpdated()
                                            .onErrorResume(error -> {
                                                log.error("Error adding role: {} to customer: {}. Error: {}", roleName, customerDB.getEmail(), error.getMessage());
                                                return Mono.just(0L); // NO Agregó ningún role
                                            });
                                })
                                .then(Mono.just(customerDB));
                    } else if (operation == UpdateRoleOperation.DELETE) {
                        return Flux.fromIterable(roleTables)
                                .flatMap(roleName -> {
                                    log.info("Removing role: {} from customer: {}", roleName, customerDB.getEmail());
                                    return databaseClient.sql(DELETE_FROM_CUSTOMER_ROLE_QUERY)
                                            .bind("customerId", customerDB.getId())
                                            .bind("roleName", roleName)
                                            .fetch()
                                            .rowsUpdated();
                                })
                                .then(Mono.just(customerDB));
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid operation: " + operation));
                    }
                });
    }

    private static final String SELECT_BY_ROLE_QUERY = """
            SELECT r.name, r.description
            FROM role r
            JOIN customer_role cr ON r.name = cr.role_name
            WHERE cr.customer_id  =:customerId
            """;

    private static final String INSERT_ROLE_QUERY = """
            INSERT INTO customer_role (customer_id, role_name)
            VALUES (:customerId, :roleName)
            """;

    private static final String DELETE_FROM_CUSTOMER_ROLE_QUERY = """
            DELETE FROM customer_role
            WHERE customer_id = :customerId AND role_name = :roleName
            """;

}
