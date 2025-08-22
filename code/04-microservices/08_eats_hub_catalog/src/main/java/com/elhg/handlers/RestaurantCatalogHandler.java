package com.elhg.handlers;

import com.elhg.dtos.responses.RestaurantResponse;
import com.elhg.enums.PriceEnum;
import com.elhg.services.definitions.RestaurantBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;


@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantCatalogHandler {

    private final RestaurantBusinessService restaurantBusinessService;

    public Mono<ServerResponse> getAllRestaurants(ServerRequest serverRequest) {
        log.info("Fetching all restaurants");
        final Integer page = serverRequest.queryParam("page")
                .map(Integer::parseInt)
                .orElse(1);
        final Integer size = serverRequest.queryParam("size")
                .map(Integer::parseInt)
                .orElse(10);

        final var restaurantFlux = restaurantBusinessService.readAll(page, size);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(restaurantFlux, RestaurantResponse.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getRestaurantByName(ServerRequest serverRequest) {
        log.info("Fetching restaurant by name");
        final var restaurantName = serverRequest.pathVariable("name");
        final var restaurantFlux = restaurantBusinessService.readByName(restaurantName);
        return restaurantFlux.hasElements()
                .flatMap(hasElements -> {
                    if (hasElements) {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(restaurantFlux, RestaurantResponse.class);
                    } else {
                        return ServerResponse.notFound().build();
                    }
                });
    }

    public Mono<ServerResponse> getRestaurantByCousinType(ServerRequest serverRequest) {
        log.info("Fetching restaurant by cousinType");

        final var cousinType = serverRequest.queryParam("cousinType").orElse("");
        final var restaurantFlux = restaurantBusinessService.readByCuisineType(cousinType);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(restaurantFlux, RestaurantResponse.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getRestaurantBetweenRange(ServerRequest serverRequest) {
        log.info("Fetching restaurants between range");

        final var pricesOpt = serverRequest.queryParam("prices"); //LOW, CHEAP
        if (pricesOpt.isEmpty() || pricesOpt.get().isBlank()) {
            return ServerResponse.badRequest().bodyValue("cousinType query parameter is required");
        }
        final var pricesType = pricesOpt.get();

        final var typeList = Arrays.stream(pricesType.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(PriceEnum::valueOf)
                .toList();

        final var restaurantFlux = restaurantBusinessService.readByPriceRangeIn(typeList);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(restaurantFlux, RestaurantResponse.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getRestaurantByCity(ServerRequest serverRequest) {
        log.info("Fetching restaurant by City");
        final var city = serverRequest.queryParam("city");
        if (city.isEmpty() || city.get().isBlank()) {
            return ServerResponse.badRequest().bodyValue("city query parameter is required");
        }
        final var restaurantFlux = restaurantBusinessService.readByCity(city.get());
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(restaurantFlux, RestaurantResponse.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
