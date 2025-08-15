package com.elhg.clients;

import com.elhg.dtos.ReservationRequest;
import com.elhg.dtos.ReservationResourceResponse;
import com.elhg.dtos.ReservationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Function;

@Service
@Slf4j
public class ReservationCrudClientImpl implements ReservationCrudClient{

    private final WebClient webClient;

    private static final String RESOURCE = "catalog/reservation";
    private static final String ERROR_MSG_4XX = "Error while creating reservation";
    private static final String ERROR_MSG_5XX = "Error while calling reservation service";


    public ReservationCrudClientImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public Mono<ReservationResourceResponse> create(ReservationRequest reservationRequest) {
        log.info("Creating reservation: {}", reservationRequest);

        return webClient.post()
                .uri(RESOURCE)
                .bodyValue(reservationRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, getClientResponseMonoFunction(ERROR_MSG_4XX))
                .onStatus(HttpStatusCode::is5xxServerError, getClientResponseMonoFunction(ERROR_MSG_5XX))
                .bodyToMono(ReservationResourceResponse.class)
                //.map(ReservationResourceResponse::getResource)
                .doOnSuccess(response -> log.info("Reservation created successfully: {}", response))
                .doOnSubscribe(subscription -> log.info("Subscribe reservation creation process: {}", subscription));
    }


    @Override
    public Mono<ReservationResponse> read(String id) {
        log.info("Reading reservation with id: {}", id);
        return webClient.get()
                .uri(RESOURCE + "/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, getClientResponseMonoFunction(ERROR_MSG_4XX))
                .bodyToMono(ReservationResponse.class)
                .doOnSuccess(response -> log.info("Reservation retrieved successfully: {}", response))
                .doOnError(error -> log.error("Error retrieving reservation with id {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<ReservationResponse> update(String id, ReservationRequest reservationRequest) {
        log.info("Updating reservation with id: {}, request: {}", id, reservationRequest);
        return webClient.put()
                .uri(RESOURCE + "/{id}", id)
                .bodyValue(reservationRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, getClientResponseMonoFunction(ERROR_MSG_4XX))
                .onStatus(HttpStatusCode::is5xxServerError, getClientResponseMonoFunction(ERROR_MSG_5XX))
                .bodyToMono(ReservationResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException.ServiceUnavailable))
                .doOnSuccess(response -> log.info("Reservation updated successfully: {}", response))
                .doOnError(error -> log.error("Error updating reservation with id {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Void> delete(String id) {
        log.info("Deleting reservation with id: {}", id);

        return webClient.delete()
                .uri(RESOURCE + "/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, getClientResponseMonoFunction(ERROR_MSG_4XX))
                .onStatus(HttpStatusCode::is5xxServerError, getClientResponseMonoFunction(ERROR_MSG_5XX))
                .bodyToMono(Void.class)
                .doOnSuccess(response -> log.info("Reservation deleted successfully with id: {}", id))
                .doOnError(error -> log.error("Error deleting reservation with id {}: {}", id, error.getMessage()));

    }


    private static Function<ClientResponse, Mono<? extends Throwable>> getClientResponseMonoFunction(String errorMsg) {
        return response -> {
            log.error("Error : {}",errorMsg);
            return Mono.error(new IllegalArgumentException(errorMsg));
        };
    }
}
