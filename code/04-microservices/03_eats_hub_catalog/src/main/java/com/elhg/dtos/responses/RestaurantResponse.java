package com.elhg.dtos.responses;

import com.elhg.enums.PriceEnum;
import com.elhg.records.Address;
import com.elhg.records.ContactInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private String name;
    private Address address;
    private String cuisineType;
    private PriceEnum priceRange;
    private String openHours;
    private String logoUrl;
    private String closeAt;
    private ContactInfo contactInfo;
    private Double globalRating;
}
