package com.elhg._13HotColdpublishers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

/*
Cold Publisher: Cada suscriptor recibe todos los datos desde el principio,
                como si tuviera su propia copia del stream.
                Ejemplo típico: Flux.range, Flux.fromIterable,
                        o cualquier Flux/Mono que genera datos bajo demanda
                        para cada suscriptor.

Hot Publisher: Los datos se emiten independientemente de los suscriptores.
                Los nuevos suscriptores solo reciben los datos emitidos
                después de su suscripción.
                Ejemplo: un Flux convertido a hot con .publish().autoConnect()
                        o usando Sinks.
 */

@Slf4j
public class MainHotColdPublishers {

    public static void main(String[] args) throws InterruptedException {
        log.info("Cold Subscriptor 1 - Subscrito");
        coldPublisher().subscribe(i -> log.info("Subscriptor 1 - Valor recibido: " + i));

        log.info("Cold Subscriptor 2 - Subscrito");
        coldPublisher().subscribe(i -> log.info("Subscriptor 2 - Valor recibido: " + i));

        log.info("Cold Subscriptor 3 - Subscrito");
        coldPublisher().subscribe(i -> log.info("Subscriptor 3 - Valor recibido: " + i));

        log.info("Hot Subscriptor 4 - Subscrito");
        hotPublisher().subscribe(i -> log.info("Subscriptor 4 - Valor recibido: " + i));

        Thread.sleep(2000);
        log.info("Hot Subscriptor 5 - Subscrito");
        hotPublisher().subscribe(i -> log.info("Subscriptor 5 - Valor recibido: " + i));

        Thread.sleep(1000);
        log.info("Hot Subscriptor 6 - Subscrito");
        hotPublisher().subscribe(i -> log.info("Subscriptor 6 - Valor recibido: " + i));

        Thread.sleep(10000);
    }

    public static Flux<Integer> coldPublisher() {
        return Flux.range(1, 10)
                .map(i -> i * 2);
    }

    public static Flux<Long> hotPublisher() {
        return Flux.interval(Duration.ofMillis(1000)).publish().autoConnect();
    }
}
