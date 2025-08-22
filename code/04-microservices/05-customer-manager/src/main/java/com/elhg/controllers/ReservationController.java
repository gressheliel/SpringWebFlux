package com.elhg.controllers;


import com.elhg.clients.ReservationCrudClient;
import com.elhg.dtos.ReservationRequest;
import com.elhg.dtos.ReservationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reservations", description = "Reservation management API")
public class ReservationController {
    private final ReservationCrudClient reservationCrudClient;

    @Operation(summary = "Create a new reservation", description = "Creates a new reservation with the provided details.")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(summary = "Get reservation by ID", description = "Retrieves the reservation details for the specified ID.")
    @GetMapping(path="{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation retrieved successfully",
                 content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid reservation ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(summary = "Update an existing reservation", description = "Updates the reservation details for the specified ID.")
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

    @Operation(summary = "Delete a reservation", description = "Deletes the reservation for the specified ID.")
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