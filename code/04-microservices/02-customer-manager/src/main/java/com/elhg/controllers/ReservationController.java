package com.elhg.controllers;


import com.elhg.clients.ReservationCrudClient;
import com.elhg.dtos.ReservationRequest;
import com.elhg.dtos.ReservationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path="reservation")
public class ReservationController {
    private final ReservationCrudClient reservationCrudClient;

    @PostMapping
    public Mono<ResponseEntity<Object>> postReservation(@Valid @RequestBody ReservationRequest reservationRequest) {
        log.info("Creating reservation: {}", reservationRequest);
        return reservationCrudClient.create(reservationRequest)
                .map(reservationResourceResponse -> ResponseEntity
                        .created(URI.create(reservationResourceResponse.getResource())).build())
                .onErrorResume(IllegalArgumentException.class,throwable -> {
                    log.error("POST Illegal argument error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class,throwable -> {
                    log.error("POST Runtime error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });

    }

    @GetMapping(path="{id}")
    public Mono<ResponseEntity<ReservationResponse>> getReservation(@PathVariable String id) {
        log.info("Get reservation id: {} ", id);
        return reservationCrudClient.read(id)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, throwable -> {
                    log.error("GET Illegal argument error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, throwable -> {
                    log.error("GET Runtime error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PutMapping(path="{id}")
    public Mono<ResponseEntity<ReservationResponse>> putReservation(@PathVariable String id,
                                                                    @Valid @RequestBody ReservationRequest reservationRequest) {
        log.info("Updating reservation id: {} with request: {}", id, reservationRequest);
        return reservationCrudClient.update(id, reservationRequest)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, throwable -> {
                    log.error("PUT Illegal argument error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, throwable -> {
                    log.error("PUT Runtime error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @DeleteMapping(path="{id}")
    public Mono<ResponseEntity<Object>> deleteReservation(@PathVariable String id) {
        log.info("Deleting reservation id: {} ", id);
        return reservationCrudClient.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(IllegalArgumentException.class, throwable -> {
                    log.error("DELETE Illegal argument error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, throwable -> {
                    log.error("DELETE Runtime error reservation: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

}