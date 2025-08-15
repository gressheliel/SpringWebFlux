package com.elhg.repositories;

import com.elhg.tables.CustomerRoleTable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CustomerRoleRepository extends R2dbcRepository<CustomerRoleTable, Void> {

}
