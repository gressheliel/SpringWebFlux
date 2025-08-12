package com.elhg.services.definitions;

import com.elhg.dtos.requests.ReservationRequest;
import com.elhg.dtos.responses.ReservationResponse;
import com.elhg.enums.ReservationStatusEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReservationBusinessService {
    Mono<String> createReservation(ReservationRequest reservationRequest);
    Flux<ReservationResponse> readByRestaurantId(UUID restaurantId, ReservationStatusEnum status);
    Mono<ReservationResponse> readByReservationId(UUID id);
    Mono<ReservationResponse> updateReservation(UUID id, ReservationRequest reservationRequest);
    Mono<Void> deleteReservation(UUID id);

}
