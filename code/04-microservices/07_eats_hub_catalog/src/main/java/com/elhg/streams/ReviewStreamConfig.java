package com.elhg.streams;



import com.elhg.dtos.events.ReviewEvent;
import com.elhg.services.impls.ReviewStreamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ReviewStreamConfig {

    private final ObjectMapper objectMapper;
    private final ReviewStreamService reviewStreamService;

    /* Recibe eventos de reseñas desde el topic review-events */
    @Bean
    public Consumer<Flux<Message<String>>> reviewEventConsumer(){
        return flux -> flux
                .doOnNext(msg -> log.info("Received review event: {}", msg.getPayload()))
                .flatMap(this::processMessage)
                .doOnError(error -> log.error("Error processing review event: {}", error.getMessage()))
                .retry(3)
                .subscribe();
    }

    /* Procesa cada mensaje de reseña */
    public Mono<Object> processMessage(Message<String> message) {
        return Mono.fromCallable(() -> {
                    String payload = message.getPayload();
                    return objectMapper.readValue(payload, ReviewEvent.class);
                })
                .flatMap(reviewStreamService::processReview)
                .onErrorResume(error -> {
                    log.error("Error processing review message: {}", error.getMessage());
                    return Mono.empty();
                });
    }
}
