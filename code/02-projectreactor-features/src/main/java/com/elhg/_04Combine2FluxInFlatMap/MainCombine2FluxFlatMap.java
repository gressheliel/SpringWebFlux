package com.elhg._04Combine2FluxInFlatMap;

import reactor.core.publisher.Flux;

public class MainCombine2FluxFlatMap {
    public static void main(String[] args) {
        Flux<String> flux1 = Flux.just("1", "2", "3");
        Flux<String> flux2 = Flux.just("A", "B", "C");

        Flux<String> combinedFlux = flux1.flatMap(
            value1 -> flux2.map(value2 -> value1 +" - "+ value2)
        );
        combinedFlux.subscribe(System.out::println);
    }
}
