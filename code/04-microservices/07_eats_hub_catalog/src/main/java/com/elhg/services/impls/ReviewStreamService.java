package com.elhg.services.impls;


import com.elhg.dtos.Review;
import com.elhg.dtos.events.ReviewEvent;
import com.elhg.services.definitions.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewStreamService {

    private final ReviewService reviewService;

    public Mono<Object> processReview(ReviewEvent reviewEvent){
        return Mono
                .fromRunnable(() -> log.info("Processing review: {}", reviewEvent))
                .then(Mono.defer(() -> {
                    try{
                        Review review = Review.builder()
                                .customerId(reviewEvent.getUuidCustomer())
                                .customerName(reviewEvent.getUuidCustomer())
                                .rating(reviewEvent.getRating())
                                .timestamp(Instant.ofEpochMilli(reviewEvent.getTimestamp()))
                                .comment(reviewEvent.getComment())
                                .build();

                        UUID restaurantId = UUID.fromString(reviewEvent.getIdRestaurant());

                        return reviewService.addRestaurantReview(restaurantId, review)
                                .doOnSuccess(aVoid -> log.info("Review processed successfully: {}", review));
                    } catch (IllegalArgumentException e) {
                        log.error("Error processing review: {}", e.getMessage());
                        return Mono.error(e);
                    }
                }));

    }
}
