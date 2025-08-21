package com.elhg.services.impls;


import com.elhg.dtos.responses.RestaurantResponse;
import com.elhg.enums.PriceEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogCacheService {
    private final ReactiveRedisTemplate<String, RestaurantResponse> redisTemplate;
    private final ReactiveRedisTemplate<String, List<RestaurantResponse>> redisListTemplate;

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(60);
    private static final String KEY_PREFIX = "restaurant:";

    /**
     * Retrieves a cached restaurant by its key.
     *
     * @param key the key of the restaurant to retrieve
     * @return a Mono containing the RestaurantResponse if found, or empty if not found
     */
    public Mono<RestaurantResponse> getCacheRestaurant(String key) {
        return redisTemplate.opsForValue()
                .get(KEY_PREFIX + key)
                .doOnNext(restaurant -> log.info("Retrieved cached restaurant: {}", restaurant.toString()))
                .doOnSubscribe(subscription -> log.info("Looking cached restaurant: {}", subscription.toString()))
                .doOnError(error -> log.error("Error : {} , to retrieve cached restaurant with key: {}",  error.getMessage(),key));
    }

    /**
     * Caches a restaurant with a specified key.
     *
     * @param keyRestaurant the key under which to cache the restaurant
     * @param restaurant    the RestaurantResponse to cache
     * @return a Mono containing the cached RestaurantResponse
     */
    public Mono<RestaurantResponse> cacheRestaurant(String keyRestaurant, RestaurantResponse restaurant) {
        return redisTemplate.opsForValue()
                .set(KEY_PREFIX + keyRestaurant, restaurant, DEFAULT_TTL)
                .thenReturn(restaurant)
                .doOnSubscribe(subscription -> log.info("Cached restaurant : {}",subscription.toString()))
                .doOnError( error -> log.error("Error caching restaurant with ID: {}: {}", keyRestaurant, error.getMessage()));
    }

    /**
     * Retrieves a list of cached restaurants by their key.
     *
     * @param key the key of the restaurant list to retrieve
     * @return a Flux containing the RestaurantResponse objects in the cached list
     */
    public Flux<RestaurantResponse> getCacheRestaurantsList(String key) {
        return redisListTemplate.opsForValue()
                .get(KEY_PREFIX + key)
                .flatMapMany(Flux::fromIterable)
                .doOnNext(restaurant -> log.info("Retrieved restaurant from cached list under key: {}", key))
                .doOnSubscribe(subscription -> log.info("Looking cached restaurant list under key: {}", key))
                .doOnError(error -> log.error("Error retrieving cached restaurant list under key: {}: {}", key, error.getMessage()));
    }

    /**
     * Caches a list of restaurants under a specified key.
     *
     * @param key         the key under which to cache the list of restaurants
     * @param restaurants the list of RestaurantResponse objects to cache
     * @return a Flux containing the cached RestaurantResponse objects
     */
    public Flux<RestaurantResponse> cacheRestaurantsList(String key, Flux<RestaurantResponse> restaurants) {
        return restaurants.collectList()
                .flatMapMany(list -> redisListTemplate.opsForValue()
                        .set(KEY_PREFIX + key, list, DEFAULT_TTL)
                        .thenMany(Flux.fromIterable(list)))
                .doOnSubscribe(subscription -> log.info("Cached restaurant list under key: {}", key))
                .doOnError(error -> log.error("Error caching restaurant list under key: {}: {}", key, error.getMessage()));
    }



    public Mono<Boolean> evictCacheRestaurant(String key){
        return redisTemplate.opsForValue()
                .delete(KEY_PREFIX + key)
                .doOnNext(deleted -> {
                    if(deleted) {
                        log.info("Evicted cached restaurant with key: {}", key);
                    } else {
                        log.warn("No cached restaurant found with key: {}", key);
                    }
                })
                .doOnError(error -> log.error("Error evicting cached restaurant with ID: {}: {}", key, error.getMessage()));
    }

    public Mono<Void> evictAllCacheRestaurant(){
        return redisTemplate.getConnectionFactory()
                .getReactiveConnection()
                .serverCommands()
                .flushAll()
                .then(Mono.fromRunnable(()-> log.info("All cached restaurants have been evicted.")));
    }


    public static String buildNameKey(String name) {
        return "name:" + name.toLowerCase();
    }

    public static String buildCuisineTypeKey(String cuisineType) {
        return "cuisine:" + cuisineType.toLowerCase();
    }

    public static String buildCityKey(String city) {
        return "city:" + city.toLowerCase();
    }

    public static String buildPriceKey(List<PriceEnum> priceEnumList) {
        return priceEnumList.stream()
                .map(PriceEnum::toString)
                .sorted()
                .collect(Collectors.joining(","));
    }

}
