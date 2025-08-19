package com.elhg.repositories;

import com.elhg.tables.CustomerTable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends R2dbcRepository<CustomerTable, Long> {

    @Query(value = "SELECT COUNT(*) > 0 FROM customer WHERE email = :email")
    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT * FROM customer WHERE email = :email")
    Mono<CustomerTable> findByEmail(String email);

}
