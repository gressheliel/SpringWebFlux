package com.elhg.clients;

import com.elhg.dtos.ReservationRequest;
import com.elhg.dtos.ReservationResourceResponse;
import com.elhg.dtos.ReservationResponse;
import reactor.core.publisher.Mono;

public interface ReservationCrudClient {
    Mono<ReservationResourceResponse> create(ReservationRequest reservationRequest);
    Mono<ReservationResponse> read(String id);
    Mono<ReservationResponse> update(String id, ReservationRequest reservationRequest);
    Mono<Void> delete(String id);
}
