package com.elhg.records;

import lombok.Builder;

@Builder
public record ContactInfo(String phone, String email, String website)
{}