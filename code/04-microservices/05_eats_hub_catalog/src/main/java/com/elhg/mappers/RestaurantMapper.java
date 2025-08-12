package com.elhg.mappers;

import com.elhg.collections.RestaurantCollection;
import com.elhg.dtos.Review;
import com.elhg.dtos.responses.RestaurantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    @Mapping(target = "globalRating", expression = "java(calculateGlobalRating(restaurantCollection.getReviews()))")
    RestaurantResponse toRestaurantResponse(RestaurantCollection restaurantCollection);

    default Flux<RestaurantResponse> toRestaurantResponseFlux(Flux<RestaurantCollection> restaurantCollections) {
        return restaurantCollections.map(this::toRestaurantResponse);
    }

    default Mono<RestaurantResponse> toRestaurantResponseMono(Mono<RestaurantCollection> restaurantCollection) {
        return restaurantCollection.map(this::toRestaurantResponse);
    }

    default Double calculateGlobalRating(List<Review> reviews){
        if(Objects.isNull(reviews) || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .filter(review-> Objects.nonNull(review.getRating()))
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
