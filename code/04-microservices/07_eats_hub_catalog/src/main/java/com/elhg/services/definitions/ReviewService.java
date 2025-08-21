package com.elhg.services.definitions;

import com.elhg.collections.RestaurantCollection;
import com.elhg.dtos.Review;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReviewService {
    Mono<RestaurantCollection> addRestaurantReview(UUID idRestaurant, Review review);
}
