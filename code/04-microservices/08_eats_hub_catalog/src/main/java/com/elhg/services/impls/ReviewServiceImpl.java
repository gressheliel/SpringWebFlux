package com.elhg.services.impls;

import com.elhg.collections.RestaurantCollection;
import com.elhg.dtos.Review;
import com.elhg.repositories.RestaurantRepository;
import com.elhg.services.definitions.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final RestaurantRepository restaurantRepository;

    public Mono<RestaurantCollection> addRestaurantReview(UUID idRestaurant, Review review){
        log.info("Adding review to restaurant with ID: {}", idRestaurant);

        return restaurantRepository
                .findById(idRestaurant)
                .switchIfEmpty(Mono.error(new RuntimeException("Restaurant not found with ID: " + idRestaurant)))
                .flatMap(restaurantDB -> {
                    if(Objects.isNull(restaurantDB.getReviews())){
                        log.info("Initializing reviews list for restaurant: {}", restaurantDB.getName());
                        restaurantDB.setReviews(new java.util.ArrayList<>());
                    }
                    restaurantDB.getReviews().add(review);
                    return restaurantRepository.save(restaurantDB);
                })
                .doOnSuccess(updatedRestaurant -> log.info("Review added successfully to restaurant: {}", updatedRestaurant.getName()))
                .doOnError(error -> log.error("Error adding review to restaurant: {}", error.getMessage()));
    }
}
