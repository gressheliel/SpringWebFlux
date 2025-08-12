package com.elhg.services.definitions;

import com.elhg.dtos.responses.RestaurantResponse;
import com.elhg.enums.PriceEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RestaurantBusinessService {
    Flux<RestaurantResponse> readAll();
    Flux<RestaurantResponse> readByCuisineType(String cuisineType);
    Flux<RestaurantResponse> readByName(String name);
    Flux<RestaurantResponse> readByPriceRangeIn(List<PriceEnum> priceRanges);
    Flux<RestaurantResponse> readByCity(String city);
}
