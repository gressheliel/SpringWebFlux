package com.elhg.services;


import com.elhg.dtos.RatingRequest;
import com.elhg.exceptions.RetryableStreamException;
import com.elhg.exceptions.StreamTerminateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {

    private final Sinks.Many<Message<String>> ratingRequestSink;
    private final ObjectMapper objectMapper;

    public Mono<Object> sendRatingToStream(RatingRequest request) {
        log.info("Service Sending rating request: {}", request);

        return Mono.fromCallable(() -> objectMapper.writeValueAsString(request))
                        .flatMap(json -> {
                            Message<String> message = MessageBuilder.withPayload(json)
                                    .setHeader(KafkaHeaders.KEY, request.getIdRestaurant().toString().getBytes())
                                    .setHeader("contentType", "text/plain")
                                    .build();

                            Sinks.EmitResult result = ratingRequestSink.tryEmitNext(message);

                            if (result.isFailure()) {
                                log.error("Failed to emit message to sink: {}", result);
                                return Mono.error(mapEmitResultToException(result));
                            } else {
                                log.info("Service Successfully emitted message to sink");
                                return Mono.empty();
                            }

                        })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .filter(throwable -> throwable instanceof RetryableStreamException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
                .doOnError(throwable -> log.error("Error sending rating request to stream: {}", throwable.getMessage()));

    }


    private RuntimeException mapEmitResultToException(Sinks.EmitResult result) {
        return switch (result) {
            case FAIL_OVERFLOW, FAIL_NON_SERIALIZED -> new RetryableStreamException("Transient Sink");
            case FAIL_CANCELLED, FAIL_TERMINATED -> new StreamTerminateException("Terminated Sink");
            default -> new RuntimeException("Unknown error occurred while emitting to sink");
        };
    }
}
