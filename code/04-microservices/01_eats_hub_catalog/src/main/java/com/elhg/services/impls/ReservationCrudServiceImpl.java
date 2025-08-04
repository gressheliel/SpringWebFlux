package com.elhg.services.impls;

import com.elhg.collections.ReservationCollection;
import com.elhg.enums.ReservationStatusEnum;
import com.elhg.exceptions.ResourceNotFoundException;
import com.elhg.repositories.ReservationRepository;
import com.elhg.repositories.RestaurantRepository;
import com.elhg.services.definitions.ReservationCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationCrudServiceImpl implements ReservationCrudService {
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;

    // flatMap combina 2 Mono/Flux en uno solo
    // Mono<RestaurantCollection> findById(ID id)  => Mono<ReservationCollection> createReservation(ReservationCollection reservation);
    @Override
    public Mono<ReservationCollection> createReservation(ReservationCollection reservation) {
        return restaurantRepository
                .findById(UUID.fromString(reservation.getRestaurantId()))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Restaurant not found with ID: " + reservation.getRestaurantId())))
                .flatMap(restaurant -> {
                    if(Objects.isNull(reservation.getStatus())){
                        reservation.setStatus(ReservationStatusEnum.PENDING);
                    }

                    log.info("Creating reservation with id {}", reservation.getId());
                    return this.reservationRepository.save(reservation);
                });

    }


    // flatMapMany se usa para transformar un Mono en un Flux
    // Mono<RestaurantCollection> findById(ID id)  => Flux<T> findByRestaurantId(UUID restaurantId);
    @Override
    public Flux<ReservationCollection> readByRestaurantId(UUID restaurantId, ReservationStatusEnum status) {
        return restaurantRepository
                .findById(restaurantId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId)))
                .flatMapMany(restaurant -> {
                    if (Objects.isNull(status)) {
                        log.info("Fetching all reservations for restaurant with id {}", restaurantId);
                        return reservationRepository.findByRestaurantId(restaurantId);
                    } else {
                        log.info("Fetching reservations with status {} for restaurant with id {}", status, restaurantId);
                        return reservationRepository.findByRestaurantIdAndStatus(restaurantId, status);
                    }
                });
    }


    @Override
    public Mono<ReservationCollection> readByReservationId(UUID id) {
        return reservationRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Reservation not found with ID: " + id)))
                .doOnNext(reservation -> log.info("Found reservation with id {}", reservation.getId()));
    }

    @Override
    public Mono<ReservationCollection> updateReservation(UUID id, ReservationCollection reservation) {
        return reservationRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Reservation not found with ID: " + id)))
                .flatMap(existingReservation -> {
                    existingReservation.setStatus(reservation.getStatus());
                    existingReservation.setNotes(reservation.getNotes());
                    existingReservation.setDate(reservation.getDate());
                    existingReservation.setTime(reservation.getTime());
                    existingReservation.setCustomerName(reservation.getCustomerName());

                    existingReservation.setPartySize(reservation.getPartySize());

                    log.info("Updating reservation with id {}", existingReservation.getId());
                    return reservationRepository.save(existingReservation);
                });

    }

    @Override
    public Mono<Void> deleteReservation(UUID id) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Reservation not found with ID: "+id)))
                .flatMap(existingReservation -> {
                    log.info("Deleting reservation with id {}", existingReservation.getId());
                    return reservationRepository.delete(existingReservation);
                });
    }
}
