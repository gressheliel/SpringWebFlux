package com.elhg.tables;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_role")
public class CustomerRoleTable {

    @Column(value = "customer_id")
    private Long customerId;

    @Column(value = "role_name")
    private String roleName;
}
