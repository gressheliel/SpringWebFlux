package com.elhg.collections;

import com.elhg.enums.ReservationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Document(collection = "reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCollection {
    @Id
    private UUID id;

    @Indexed
    private String restaurantId;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String date;
    private String time;
    private Integer partySize;

    @Indexed
    private ReservationStatusEnum status;

    private String notes;
}
