package com.elhg.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    private String restaurantId;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String dateTime; // example 2025-06-16,15:30
    private Integer partySize;
    private String comment;
}

