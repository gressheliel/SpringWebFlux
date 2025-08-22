package com.elhg.handlers;

import com.elhg.dtos.requests.ReservationRequest;
import com.elhg.services.definitions.ReservationBusinessService;
import com.elhg.validators.ReactiveValidator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReservationHandler {
    private final ReactiveValidator reactiveValidator;
    private final ReservationBusinessService reservationBusinessService;


    public Mono<ServerResponse> postReservation (ServerRequest serverRequest){
        log.info("Received request to create reservation: {}", serverRequest);
        return serverRequest.bodyToMono(ReservationRequest.class)
                .flatMap(reactiveValidator::validate)
                .flatMap(reservationBusinessService::createReservation)
                .flatMap(reservation ->
                        ServerResponse.created(URI.create("/reservations/" + reservation))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("Resource", "/reservation/" + reservation)))
                .switchIfEmpty(ServerResponse.badRequest().build())
                .doOnError(e -> log.error("Error creating reservation: {}", e.getMessage()))
                .doOnSuccess(response -> log.info("Reservation created successfully: {}", response.statusCode()));
    }

    public Mono<ServerResponse> getReservationById (ServerRequest serverRequest){
        final var reservationId = serverRequest.pathVariable("id");
        return parseUUID(reservationId)
                .flatMap(reservationBusinessService::readByReservationId)
                .flatMap(reservationFounded ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(reservationFounded))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(e -> log.error("Error fetching reservation: {}", e.getMessage()))
                .doOnSuccess(response -> log.info("Fetched reservation successfully: {}", response.statusCode()));
    }

    public Mono<ServerResponse> updateReservation (ServerRequest serverRequest){
        final var reservationId = serverRequest.pathVariable("id");
        return parseUUID(reservationId)
                 .flatMap(uuid -> serverRequest.bodyToMono(ReservationRequest.class)
                        .flatMap(reactiveValidator::validate)
                        .flatMap(reservationRequest -> reservationBusinessService.updateReservation(uuid, reservationRequest)))
                .flatMap(updatedReservation ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updatedReservation))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(e -> log.error("Error updating reservation: {}", e.getMessage()))
                .doOnSuccess(response -> log.info("Reservation updated successfully: {}", response.statusCode()));
    }

    public Mono<ServerResponse> deleteReservation (ServerRequest serverRequest){
        final var reservationId = serverRequest.pathVariable("id");
        return  parseUUID(reservationId)
                .flatMap(reservationBusinessService::deleteReservation)
                .then(ServerResponse.noContent().build())
                .doOnError(e -> log.error("Error deleting reservation: {}", e.getMessage()))
                .doOnSuccess(response -> log.info("Reservation deleted successfully: {}", response.statusCode()));
    }

    private Mono<UUID> parseUUID(String uuid){
        return Mono.justOrEmpty(uuid)
                .map(UUID::fromString)
                .switchIfEmpty(Mono.error(new ValidationException("Invalid UUID format: " + uuid)));
    }

}
