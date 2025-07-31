package com.elhg._05Combine2FluxMergeConcat;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class MainCombine2FluxMergeConcat {
    public static void main(String[] args) {
        Flux<String> flux1 = Flux.just("1", "2").delayElements(Duration.ofMillis(100));
        Flux<String> flux2 = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(50));

        //Flux<String> combinedFlux = Flux.merge(flux1, flux2);
        Flux<String> combinedFlux = Flux.concat(flux1, flux2);

        /*
        Flux.merge combina múltiples flujos emitiendo los elementos tan pronto como estén disponibles,
                    intercalando valores si los flujos son asíncronos.
        Flux.concat combina los flujos de manera secuencial, emitiendo todos los elementos del primer
                    flujo antes de pasar al siguiente.
        merge: concurrencia, mezcla de elementos.
        concat: secuencial, orden garantizado.
         */

        combinedFlux.doOnNext(System.out::println).blockLast();
    }
}
