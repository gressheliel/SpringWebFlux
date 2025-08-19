package com.elhg.tables;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class CustomerTable {

    @Id
    private Long id;
    @Column(value="firstname")
    private String firstName;

    private Boolean enabled;

    @Column(value="account_non_expired")
    private Boolean accountNonExpired;

    @Column(value="credentials_non_expired")
    private Boolean credentialsNonExpired;

    @Column(value = "lastname")
    private String lastName;

    private String email;

    @Column(value = "user_password")
    private String userPassword;

    private int age;

    @Column(value = "birthdate")
    private LocalDate birthdate;

    @Column(value = "alternative_id")
    private UUID alternativeId;
}
