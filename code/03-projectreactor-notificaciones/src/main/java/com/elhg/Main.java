package com.elhg;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Ejemplo Map 1 -> 1 : UpperCase");
        List<String> listNames = List.of("Eliel", "July", "Ian", "Daniel", "Benjamin");
        listNames
                .stream()
                .map(String::toUpperCase)
                .forEach(System.out::print);

        System.out.println(" \n ");
        System.out.println("Ejemplo flatMap 1 -> N : Imprime cada caracter de cada nombre, " +
                "eliminando la letra 'a' y transformando a minúsculas.");
        listNames
                .stream()
                .flatMap(name -> name.chars().mapToObj(c -> (char) c))
                .filter( c -> c!='a')
                .forEach(System.out::print);

        System.out.println(" \n ");
        System.out.println("Ejemplo map, El map transforma el valor " +
                "dentro del Mono<Boolean> en otro Mono<Boolean>, " +
                "resultando en un Mono<Mono<Boolean>>. ");
        Mono<Boolean> monoBoolean = Mono.just(true);
        monoBoolean.map(value -> Mono.just(value))
                .doOnNext(value -> System.out.println("Value: " + value))
                .doOnSuccess(value -> System.out.println("Success with value: " + value))
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .subscribe(
                        valueReceived -> System.out.println("Received value: " + valueReceived),
                        error -> System.out.println("An error occurred: " + error.getMessage()),
                        () -> System.out.println("Processing completed successfully!")
                );

        System.out.println(" \n ");
        System.out.println("Ejemplo flatMap, Usando flatMap el resultado sigue siendo un Mono<Boolean>, " +
                " no un Mono<Mono<Boolean>>.");
        Mono<Boolean> monoBoolean1 = Mono.just(true);
        monoBoolean1.flatMap(value -> Mono.just(value))
                .doOnNext(value -> System.out.println("Value: " + value))
                .doOnSuccess(value -> System.out.println("Success with value: " + value))
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .subscribe(
                        valueReceived -> System.out.println("Received value: " + valueReceived),
                        error -> System.out.println("An error occurred: " + error.getMessage()),
                        () -> System.out.println("Processing completed successfully!")
                );

    }
}