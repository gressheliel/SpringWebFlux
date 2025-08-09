package com.elhg.services.impls;

import com.elhg.collections.RestaurantCollection;
import com.elhg.enums.PriceEnum;
import com.elhg.records.Address;
import com.elhg.repositories.RestaurantRepository;
import com.elhg.services.definitions.RestaurantCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantCatalogServiceImpl implements RestaurantCatalogService {
    private final RestaurantRepository restaurantRepository;


    @Override
    public Flux<RestaurantCollection> readAll() {
        return restaurantRepository.findAll();
    }

    @Override
    public Flux<RestaurantCollection> readByCuisineType(String cuisineType) {
        return restaurantRepository.findByCuisineType(cuisineType)
                .doOnSubscribe(subscription -> log.info("Init search restaurants by cuisine type: {}", cuisineType))
                .doOnNext(restaurant -> log.info("Found restaurant by cuisine type: {}", restaurant.getName()))
                .onErrorResume(RestaurantCatalogServiceImpl::apply);

    }

    @Override
    public Flux<RestaurantCollection> readByName(String name) {
        return restaurantRepository.findByNameStartsWithIgnoreCase(name)
                .doOnSubscribe(subscription -> log.info("Init search restaurants by name: {}", name))
                .doOnNext(restaurant -> log.info("Found restaurant by name: {}", restaurant.getName()));
    }

    @Override
    public Flux<RestaurantCollection> readByPriceRangeIn(List<PriceEnum> priceRanges) {
        return restaurantRepository.findByPriceRangeIn(priceRanges)
                .switchIfEmpty(Flux.empty()
                        .cast(RestaurantCollection.class)
                        .doOnSubscribe(subscription -> log.info("No restaurants found for price ranges: {}", priceRanges)));
    }

    @Override
    public Flux<RestaurantCollection> readByCity(String city) {
        /*return restaurantRepository.findByAddressCity(city)
                .doOnNext(restaurantCollection -> log.info("Found restaurant in city: {}", city))
                .onErrorResume(RestaurantCatalogServiceImpl::apply);
        */
        return restaurantRepository.findAll()
                .map(RestaurantCollection::getAddress)
                .filter(Objects::nonNull)
                .map(Address::city)
                .filter(Objects::nonNull)
                .distinct()
                .collectList()
                .flatMapMany(cities -> {
                    if (cities.isEmpty()) {
                        log.warn("No restaurants found in city: {} , List of cities is empty", city);
                        return Flux.empty();
                    }

                    log.info("Init search of restaurants in city: {}", city);
                    return restaurantRepository.findByAddressCity(city)
                            .doOnNext(restaurantCollection -> log.info("Found restaurant in city: {}", city))
                            .onErrorResume(RestaurantCatalogServiceImpl::apply);
                });
    }


    private static Publisher<? extends RestaurantCollection> apply(Throwable throwable) {
        log.error("Error fetching restaurants by cuisine type: {}", throwable.getMessage());
        return Flux.empty();
    }
}
