package com.elhg.services.definitions;

import com.elhg.collections.ReservationCollection;
import com.elhg.enums.ReservationStatusEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReservationCrudService {
    Mono<ReservationCollection> createReservation(ReservationCollection reservation);
    Flux<ReservationCollection> readByRestaurantId(UUID restaurantId, ReservationStatusEnum status);
    Mono<ReservationCollection> readByReservationId(UUID id);
    Mono<ReservationCollection> updateReservation(UUID id, ReservationCollection reservation);
    Mono<Void> deleteReservation(UUID id);
}
