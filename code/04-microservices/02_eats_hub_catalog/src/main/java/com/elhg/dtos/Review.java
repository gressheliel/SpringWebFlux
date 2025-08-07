package com.elhg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Object customerId;
    private String customerName;
    private Integer rating; // Assuming rating is an integer value
    private String comment;
    private Instant timestamp;
}
