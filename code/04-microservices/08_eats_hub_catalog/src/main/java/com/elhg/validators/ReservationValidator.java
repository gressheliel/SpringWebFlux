package com.elhg.validators;

import com.elhg.clients.PlannerMSClient;
import com.elhg.collections.ReservationCollection;
import com.elhg.collections.RestaurantCollection;
import com.elhg.exceptions.BusinessException;
import com.elhg.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationValidator {
    private final PlannerMSClient plannerMSClient;
    private final RestaurantRepository restaurantRepository;


    public <T>Mono<Void> applyValidations(T input, List<BusinessValidator<T>> validations){
        if (validations == null || validations.isEmpty()) {
            log.warn("No validations provided, skipping validation.");
            return Mono.empty();
        }
        //Mono::then  Ejecuta las validaciones secuencialmente.
        return validations.stream()
                .reduce(Mono.empty(), (chain, validator) ->
                        chain.then(validator.validate(input)), Mono::then);

    }


    public BusinessValidator<ReservationCollection> validateRestaurantNotClosed() {
        log.info("Validating if the restaurant is not closed at the requested reservation time.");
        // Opcion 1
        return reservation -> restaurantRepository
                .findById(UUID.fromString(reservation.getRestaurantId()))
                .switchIfEmpty(Mono.error(new BusinessException("Restaurant not found with id: " + reservation.getRestaurantId())))
                .flatMap(restaurant -> {
                    if (isRestaurantClosed(restaurant, reservation.getTime())) {
                        return Mono.error(new BusinessException("The restaurant is closed at the requested reservation time: " + reservation.getTime()));
                    }
                    return Mono.empty();
                });

        // Opcion 2
        /*
        BusinessValidator<ReservationCollection> businessValidator =  reservation -> {
            return restaurantRepository
                    .findById(UUID.fromString(reservation.getRestaurantId()))
                    .switchIfEmpty(Mono.error(new BusinessException("Restaurant not found with id: " + reservation.getRestaurantId())))
                    .flatMap(restaurant -> {
                        if (isRestaurantClosed(restaurant, reservation.getTime())) {
                            return Mono.error(new BusinessException("The restaurant is closed at the requested reservation time: " + reservation.getTime()));
                        }
                        return Mono.empty();
                    });
        };
        return businessValidator;
         */
    }

    public BusinessValidator<ReservationCollection> validateAvailability(){
        log.info("Validating restaurant availability for the requested reservation time.");
        return reservation ->
                plannerMSClient.verifyAvailability(reservation.getDate(), reservation.getTime(), UUID.fromString(reservation.getRestaurantId()))
                        .flatMap( isAvailable -> {
                           if(!isAvailable) {
                               return Mono.error(new BusinessException("The restaurant is not available for the requested reservation time: " + reservation.getTime()));
                           }
                            return Mono.empty();
                        });
    }


    public BusinessValidator<ReservationCollection> validateRestaurantIdBeforeUpdate(){
        log.info("Validating that the restaurant ID is the same as the original restaurant ID before updating the reservation.");
        return reservation ->
                    restaurantRepository.findById(UUID.fromString(reservation.getRestaurantId()))
                        .switchIfEmpty(Mono.error(new BusinessException("Restaurant not found with id: " + reservation.getRestaurantId())))
                        .flatMap(restaurant -> {
                            log.info("restaurant.getId() :{}",restaurant.getId());
                            log.info("UUID.fromString(reservation.getRestaurantId()) : {}",UUID.fromString(reservation.getRestaurantId()));
                            var test = restaurant.getId().equals(UUID.fromString(reservation.getRestaurantId()));
                            log.info("test : {}", test);
                            if (!restaurant.getId().equals(UUID.fromString(reservation.getRestaurantId()))) {
                                return Mono.error(new BusinessException("Restaurant ID  must be the same as the original restaurant"));
                            }
                            return Mono.empty();
                        });
    }

    // devuelve true si no pasa las validaciones,  false si pasa las validaciones
    private boolean isRestaurantClosed(RestaurantCollection restaurant, String reservationTime){
        try{
            if(Objects.isNull(restaurant.getCloseAt()) || Objects.isNull(reservationTime) ){
                log.warn("Restaurant or reservation time is null, assuming restaurant is closed.");
                return true; // If we can't determine, assume closed
            }

            LocalTime closeTimeRestaurant = LocalTime.parse(restaurant.getCloseAt(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime reservationTimeParam = LocalTime.parse(reservationTime, DateTimeFormatter.ofPattern("HH:mm"));

            // Check if the restaurant is closed at the reservation time
            return reservationTimeParam.isAfter(closeTimeRestaurant);

        }catch(Exception e){
            log.error("Error checking if restaurant is closed: {}", e.getMessage());
            return true; // Default to closed if there's an error
        }
    }
}
