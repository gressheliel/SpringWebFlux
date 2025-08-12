package com.elhg.clients;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class PlannerMSClient {
    private static final String UNAVAILABLE_RESTAURANT_ID = "dfcbe98d-392b-4b93-9a49-27005223d15d";

    /*
        * Simulates a call to the Planner Microservice to verify if a restaurant is available
        * Returns Mono<True> if the restaurant is available,
        *         Mono<False> if it is not available
     */
    public Mono<Boolean> verifyAvailability(String date, String time,
                                     UUID restaurantId) {
        return Mono.fromCallable(() -> !restaurantId.toString().equals(UNAVAILABLE_RESTAURANT_ID))
                .delayElement(getRandomDuration())
                .doOnNext(sub -> log.info("Verifying availability for restaurantId: {}, date: {}, time: {}",
                        restaurantId, date, time));
    }

    Duration getRandomDuration() {
        return Duration.ofMillis(ThreadLocalRandom.current().nextInt(20, 1000));
    }
}
