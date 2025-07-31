package com.elhg._10RetryRepeat;

import com.elhg._10RetryRepeat.Console;
import com.elhg._10RetryRepeat.Videogame;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class MainRetryRepeat {

    public static void main(String[] args) {
        callFallback().subscribe(videogame -> log.info(videogame.toString()));
    }

    public static Flux<Videogame> callFallback() {
        return Database.getVideogamesFlux().handle((vg, sink) -> {
            if (Console.DISABLED == vg.getConsole() ) {
                sink.error(new RuntimeException("VideoGame DISABLED: " + vg.getName()));
                return;
            } else {
                sink.next(vg);
            }
        }).retry(5)
                .onErrorResume( error -> {
            log.error("[onErrorResume] A Database error occurred: {}", error.getMessage());
            return Database.fluxFallback;
        }).repeat(1)
                .cast(Videogame.class);
    }
}
