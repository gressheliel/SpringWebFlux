package com.elhg._07Handle;


import reactor.core.publisher.Flux;

public class MainHandle {
    public static void main(String[] args) {
        handleError().subscribe(videogame -> System.out.println("[subscribe] Received videogame: " + videogame));
    }

    public static Flux<Videogame> handleError() {
        return Database.getVideogamesFlux().handle((vg, sink) -> {
            if (Console.DISABLED == vg.getConsole() ) {
                sink.error(new RuntimeException("VideoGame DISABLED: " + vg.getName()));
                return;
            } else {
                sink.next(vg);
            }
        });
    }
}
