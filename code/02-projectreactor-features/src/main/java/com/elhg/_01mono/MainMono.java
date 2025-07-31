package com.elhg._01mono;

import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@Log
public class MainMono {
    public static void main(String[] args) {
        Mono<String> monoString = Mono.just("Hello, World!");

        //Publisher
        monoString
            .map(String::toUpperCase)
                .doOnNext(value ->log.info("[onNext] Processing value: " + value))
                .doOnSuccess(value -> log.info("[onSuccess] Processing completed successfully!"))
                .doOnError(error -> log.info("[onError] An error occurred: " + error.getMessage()));

        //Consumer
        monoString.subscribe(
                valueReceived -> log.info("[subscribe] Received value: " + valueReceived),
                error -> log.info("[subscribe] An error occurred: " + error.getMessage()),
                () -> log.info("[subscribe] Processing completed successfully!"));
    }
}