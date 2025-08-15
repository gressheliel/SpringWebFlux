package com.elhg.repositories;

import com.elhg.tables.RoleTable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface RoleRepository extends R2dbcRepository<RoleTable, String> {

}
