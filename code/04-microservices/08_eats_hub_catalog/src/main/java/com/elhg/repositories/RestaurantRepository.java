package com.elhg.repositories;

import com.elhg.collections.RestaurantCollection;
import com.elhg.enums.PriceEnum;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends ReactiveMongoRepository<RestaurantCollection, UUID> {

    //@Query("db.restaurants.find({ \"cuisineType\": \"Japanese\" })")
    Flux<RestaurantCollection> findByCuisineType(String cuisineType);
    //@Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Flux<RestaurantCollection> findByNameStartsWithIgnoreCase(String name);

    Flux<RestaurantCollection> findByPriceRangeIn(List<PriceEnum> priceRanges);
    Flux<RestaurantCollection> findByAddressCity(String city);
}
