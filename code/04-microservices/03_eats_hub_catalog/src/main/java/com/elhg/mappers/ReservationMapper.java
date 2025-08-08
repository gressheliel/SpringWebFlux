package com.elhg.mappers;

import com.elhg.collections.ReservationCollection;
import com.elhg.dtos.requests.ReservationRequest;
import com.elhg.dtos.responses.ReservationResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "dateTime", expression = "java(reservationCollection.getDate() + \",\" + reservationCollection.getTime())")
    ReservationResponse toReservationResponse(ReservationCollection reservationCollection);

    @Mapping(target = "notes", source = "comment", defaultValue = "")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "time", ignore = true)
    @Mapping(target = "status", ignore = true)
    ReservationCollection toReservationCollection(ReservationRequest reservationRequest);


    default Flux<ReservationResponse> toReservationResponseFlux(Flux<ReservationCollection> reservationCollections) {
        return reservationCollections.map(this::toReservationResponse);
    }

    default Mono<ReservationResponse> toReservationResponseMono(Mono<ReservationCollection> reservationCollection) {
        return reservationCollection.map(this::toReservationResponse);
    }

    default Flux<ReservationCollection> toReservationCollectionFlux(Flux<ReservationRequest> reservationRequestFlux) {
        return reservationRequestFlux.map(this::toReservationCollection);
    }

    default Mono<ReservationCollection> toReservationCollectionMono(Mono<ReservationRequest> reservationRequestMono) {
        return reservationRequestMono.map(this::toReservationCollection);
    }

    @AfterMapping
    default void splitDateTime(ReservationRequest reservationRequest,
                          @MappingTarget ReservationCollection reservationCollection) {
        String dateTime = reservationRequest.getDateTime();
        if (dateTime == null || dateTime.isEmpty()) {
            reservationCollection.setTime("");
            reservationCollection.setDate("");
        }

        String[] parts = dateTime.split(",");

        if (parts.length != 2) {
            reservationCollection.setTime("");
            reservationCollection.setDate("");
        }else{
            reservationCollection.setDate(parts[0].trim());
            reservationCollection.setTime(parts[1].trim());
        }
    }
}
