package com.elhg.services.definitions;

import com.elhg.collections.RestaurantCollection;
import com.elhg.enums.PriceEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RestaurantCatalogService {
    Flux<RestaurantCollection> readAll(Integer page, Integer size);
    Flux<RestaurantCollection> readByCuisineType(String cuisineType);
    Flux<RestaurantCollection> readByName(String name);
    Flux<RestaurantCollection> readByPriceRangeIn(List<PriceEnum> priceRanges);
    Flux<RestaurantCollection> readByCity(String city);
}
