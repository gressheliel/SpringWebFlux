package com.elhg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingEvent {

    private String idRestaurant;
    private Integer rating;
    private String uuidCustomer;
    private String comment;
    private String eventType; //created_event, updated_event, deleted_event

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();
}
