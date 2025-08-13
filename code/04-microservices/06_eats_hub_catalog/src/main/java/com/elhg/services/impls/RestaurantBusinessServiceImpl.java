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
    private final CatalogCacheService restaurantCacheService;

    @Override
    public Flux<RestaurantResponse> readAll(Integer page, Integer size) {
        log.info("Fetching all restaurants from the catalog page: {}, size: {}", page, size);
        return restaurantCatalogService
                .readAll(page, size)
                .transform(restaurantMapper::toRestaurantResponseFlux)
                .doOnComplete(() -> log.info("Completed fetching all restaurants"));
    }

    @Override
    public Flux<RestaurantResponse> readByCuisineType(String cuisineType) {
        log.info("Fetching restaurants by cuisine type: {}", cuisineType);
        final String cacheKey = CatalogCacheService.buildCuisineTypeKey(cuisineType);

        return restaurantCacheService.getCacheRestaurantsList(cacheKey)
                .switchIfEmpty(
                        restaurantCatalogService.readByCuisineType(cuisineType)
                                .transform(restaurantMapper::toRestaurantResponseFlux)
                                .transform(restaurantResponse ->
                                        restaurantCacheService.cacheRestaurantsList(cacheKey, restaurantResponse))
                )
                .doOnComplete(() -> log.info("Read restaurants for cuisine type: {}", cuisineType));
    }

    @Override
    public Flux<RestaurantResponse> readByName(String name) {
        log.info("Fetching restaurants by name: {}", name);
        final String cacheKey = CatalogCacheService.buildNameKey(name);

        return restaurantCacheService.getCacheRestaurantsList(cacheKey)
                .switchIfEmpty(
                        restaurantCatalogService.readByName(name)
                        .transform(restaurantMapper::toRestaurantResponseFlux)
                        .transform(restaurantResponseFlux -> restaurantCacheService.cacheRestaurantsList(cacheKey,restaurantResponseFlux))

                )
                .doOnComplete(() -> log.info("Completed fetching restaurant by name: {}", name));
    }




    @Override
    public Flux<RestaurantResponse> readByPriceRangeIn(List<PriceEnum> priceRanges) {
        log.info("Fetching restaurants by price ranges: {}", priceRanges);
        final String cacheKey = CatalogCacheService.buildPriceKey(priceRanges);

        return restaurantCacheService.getCacheRestaurantsList(cacheKey)
                   .switchIfEmpty(
                           restaurantCatalogService.readByPriceRangeIn(priceRanges)
                               .transform(restaurantMapper::toRestaurantResponseFlux)
                               .transform(restaurantResponse -> restaurantCacheService.cacheRestaurantsList(cacheKey, restaurantResponse))
                        )
                .doOnComplete(() -> log.info("Completed fetching restaurants by price ranges: {}", priceRanges));
    }

    @Override
    public Flux<RestaurantResponse> readByCity(String city) {
        log.info("Fetching restaurants by city: {}", city);
        final String cacheKey = CatalogCacheService.buildCityKey(city);

        return restaurantCacheService.getCacheRestaurantsList(cacheKey)
                .switchIfEmpty(
                        restaurantCatalogService.readByCity(city)
                                .transform(restaurantMapper::toRestaurantResponseFlux)
                                .transform(restaurantResponse -> restaurantCacheService.cacheRestaurantsList(cacheKey, restaurantResponse))
                )
                .doOnComplete(() -> log.info("Completed fetching restaurants by city: {}", city));
    }
}
