package com.elhg.services.impls;

import com.elhg.dtos.responses.RestaurantResponse;
import com.elhg.enums.PriceEnum;
import com.elhg.mappers.RestaurantMapper;
import com.elhg.services.definitions.RestaurantBusinessService;
import com.elhg.services.definitions.RestaurantCatalogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RestaurantBusinessServiceImpl implements RestaurantBusinessService {

    private final RestaurantCatalogService restaurantCatalogService;
    private final RestaurantMapper restaurantMapper;

    @Override
    public Flux<RestaurantResponse> readAll() {
        log.info("Fetching all restaurants from the catalog");
        return restaurantCatalogService
                .readAll()
                .transform(restaurantMapper::toRestaurantResponseFlux)
                .doOnComplete(() -> log.info("Completed fetching all restaurants"));
    }

    @Override
    public Flux<RestaurantResponse> readByCuisineType(String cuisineType) {
        log.info("Fetching restaurants by cuisine type: {}", cuisineType);
        return restaurantCatalogService
                .readByCuisineType(cuisineType)
                .transform(restaurantMapper::toRestaurantResponseFlux)
                .doOnComplete(() -> log.info("Completed fetching restaurants by cuisine type: {}", cuisineType));
    }

    @Override
    public Flux<RestaurantResponse> readByName(String name) {
        log.info("Fetching restaurants by name: {}", name);
        return restaurantCatalogService
                .readByName(name)
                .transform(restaurantMapper::toRestaurantResponseFlux)
                .doOnComplete(() -> log.info("Completed fetching restaurant by name: {}", name));
    }

    @Override
    public Flux<RestaurantResponse> readByPriceRangeIn(List<PriceEnum> priceRanges) {
        log.info("Fetching restaurants by price ranges: {}", priceRanges);
        return restaurantCatalogService
                .readByPriceRangeIn(priceRanges)
                .transform(restaurantMapper::toRestaurantResponseFlux)
                .doOnComplete(() -> log.info("Completed fetching restaurants by price ranges: {}", priceRanges));
    }

    @Override
    public Flux<RestaurantResponse> readByCity(String city) {
        log.info("Fetching restaurants by city: {}", city);
        return restaurantCatalogService
                .readByCity(city)
                .transform(restaurantMapper::toRestaurantResponseFlux)
                .doOnComplete(() -> log.info("Completed fetching restaurants by city: {}", city));
    }
}
