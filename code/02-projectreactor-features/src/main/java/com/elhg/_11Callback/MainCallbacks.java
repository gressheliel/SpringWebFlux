package com.elhg._11Callback;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


@Slf4j
public class MainCallbacks {
    public static void main(String[] args) {
        callbacks().subscribe();
    }

    public static Flux<Videogame> callbacks(){
        return Database.getVideogamesFlux()
                //.delayElements(Duration.ofMillis(500))
                //.timeout(Duration.ofMillis(300))
                .doOnSubscribe(subscription ->  log.info("[doOnSubscribe] Subscription started {}"))
                .doOnRequest(n-> log.info("[doOnRequest] Requesting {} items", n))
                .doOnNext(videogame -> log.info("[doOnNext] Processing videogame: {}", videogame.getName()))
                .doOnCancel(()-> log.info("[doOnCancel] Subscription cancelled"))
                .doOnError(error -> log.error("[doOnError] An error occurred: {}", error.getMessage()))
                .doOnComplete(()-> log.info("[doOnComplete] Processing completed successfully!, Only executed if no error occurs"))
                .doOnTerminate(()-> log.info("[doOnTerminate] Processing terminated, Always executed!"))
                .doFinally(signalType -> log.info("[doFinally] Processing finished with signal: {}", signalType));
    }
}
