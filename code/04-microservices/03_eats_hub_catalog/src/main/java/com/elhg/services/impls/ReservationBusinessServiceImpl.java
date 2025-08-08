package com.elhg.services.impls;

import com.elhg.collections.ReservationCollection;
import com.elhg.dtos.requests.ReservationRequest;
import com.elhg.dtos.responses.ReservationResponse;
import com.elhg.enums.ReservationStatusEnum;
import com.elhg.mappers.ReservationMapper;
import com.elhg.repositories.ReservationRepository;
import com.elhg.services.definitions.ReservationBusinessService;
import com.elhg.services.definitions.ReservationCrudService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationBusinessServiceImpl implements ReservationBusinessService {

    private final ReservationMapper reservationMapper;
    private final ReservationCrudService reservationCrudService;

    @Override
    public Mono<String> createReservation(ReservationRequest reservationRequest) {
        log.info("Creating reservation for restaurant ID: {}", reservationRequest.getRestaurantId());
        return Mono.just(reservationRequest)
                .transform(reservationMapper::toReservationCollectionMono)
                .flatMap(reservationCrudService::createReservation)
                .map(savedReservation -> String.valueOf(savedReservation.getId()))
                .doOnSuccess(id -> log.info("Successfully created reservation with ID: {}", id));

    }

    @Override
    public Flux<ReservationResponse> readByRestaurantId(UUID restaurantId, ReservationStatusEnum status) {
        log.info("Fetching reservations for restaurant ID: {} with status: {}", restaurantId, status);
        return reservationCrudService.readByRestaurantId(restaurantId, status)
                .transform(reservationMapper::toReservationResponseFlux)
                .doOnComplete(() -> log.info("Completed fetching reservations for restaurant ID: {}", restaurantId));
    }

    @Override
    public Mono<ReservationResponse> readByReservationId(UUID id) {
        log.info("Fetching reservation by ID: {}", id);
        return reservationCrudService.readByReservationId(id)
                .transform(reservationMapper::toReservationResponseMono)
                .doOnSuccess(reservationResponse -> log.info("Completed fetching reservation by ID: {}", id));
    }

    @Override
    public Mono<ReservationResponse> updateReservation(UUID id, ReservationRequest reservationRequest) {
        log.info("Updating reservation with ID: {}", id);
        return Mono.just(reservationRequest)
                .transform(reservationMapper::toReservationCollectionMono)
                .flatMap(reservationCollection -> reservationCrudService.updateReservation(id, reservationCollection))
                .transform(reservationMapper::toReservationResponseMono)
                .doOnSuccess(reservationResponse -> log.info("Successfully updated reservation with ID: {}", id));
    }

    @Override
    public Mono<Void> deleteReservation(UUID id) {
        log.info("Deleting reservation with ID: {}", id);
        return reservationCrudService.deleteReservation(id)
                .doOnSuccess(aVoid -> log.info("Successfully deleted reservation with ID: {}", id));
    }
}
