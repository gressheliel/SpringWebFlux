package com.elhg._02flux;

import lombok.extern.java.Log;
import reactor.core.publisher.Flux;

@Log
public class MainFlux {
    public static void main(String[] args) {
        Flux<String> fluxString = Flux.just("Hello", "World!", "Reactor");

        //Publisher
        fluxString
                .doOnNext(value ->log.info("[onNext] Processing value: " + value))
                .doOnComplete( () -> log.info("[onComplete] Processing completed successfully!"));

        //Consumer
        fluxString.subscribe(
                valueReceived -> log.info("[subscribe] Received value: " + valueReceived),
                error -> log.info("[subscribe] An error occurred: " + error.getMessage()),
                () -> log.info("[subscribe] Processing completed successfully!"));
    }
}