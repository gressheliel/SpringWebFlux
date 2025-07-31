package com.elhg._09OnErrorReturn;

import com.elhg._03MapFilterReduce.Console;
import com.elhg._03MapFilterReduce.Database;
import com.elhg._03MapFilterReduce.Videogame;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class MainOnErrorReturn {

    public static void main(String[] args) {
        handleErrorVideogameDefault().subscribe(videogame -> log.info("[subscribe] Received videogame: {} ", videogame));
    }

    /*
        OnErrorResume alternativa cuando ocurre un error, se emite un flujo alternativo. Ejemplo. BD o MS de respaldo
        OnErrorReturn devuelve un valor estatico y por defecto cuando ocurre un error.
     */
    public static Flux<Videogame> handleErrorVideogameDefault() {
        return Database.getVideogamesFlux().handle((vg, sink) -> {
            if (Console.DISABLED == vg.getConsole() ) {
                sink.error(new RuntimeException("VideoGame DISABLED: " + vg.getName()));
                return;
            } else {
                sink.next(vg);
            }
        }).onErrorReturn(Database.DEFAULT_VIDEOGAME)
                .cast(Videogame.class);
    }
}
