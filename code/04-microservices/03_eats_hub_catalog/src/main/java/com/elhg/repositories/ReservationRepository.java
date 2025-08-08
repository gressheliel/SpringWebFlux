package com.elhg.repositories;

import com.elhg.collections.ReservationCollection;
import com.elhg.enums.ReservationStatusEnum;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ReservationRepository extends ReactiveMongoRepository<ReservationCollection, UUID> {
    Flux<ReservationCollection> findByRestaurantId(UUID restaurantId);
    Flux<ReservationCollection> findByRestaurantIdAndStatus(UUID restaurantId, ReservationStatusEnum status);
}
