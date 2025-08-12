package com.elhg.records;

import lombok.Builder;

@Builder
public record Address (
    String street,
    String city,
    String postalCode
){}
