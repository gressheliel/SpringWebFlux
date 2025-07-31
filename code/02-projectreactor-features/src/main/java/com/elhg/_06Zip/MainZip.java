package com.elhg._06Zip;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class MainZip {
    public static void main(String[] args) {
        // Zip multiple Flux streams together

        // Simular 4 Microservicios: Orders, Shipments, Warehouses, Payments, Confirmations
        Flux<String> fluxShipments = Flux.just("Shipment 1", "Shipment 2", "Shipment 3").delayElements(Duration.ofMillis(120));
        Flux<String> fluxWarehouses = Flux.just("Warehouse A", "Warehouse B", "Warehouse C").delayElements(Duration.ofMillis(50));
        Flux<String> fluxPayments = Flux.just("Payment 1", "Payment 2", "Payment 3").delayElements(Duration.ofMillis(150));
        Flux<String> fluxConfirmations = Flux.just("Confirmation 1", "Confirmation 2", "Confirmation 3").delayElements(Duration.ofMillis(20));


        Flux<String> fluxReport = Flux.zip(fluxShipments, fluxWarehouses, fluxPayments, fluxConfirmations)
                .map(tuple -> String.format("Shipment: %s, Warehouse: %s, Payment: %s, Confirmation: %s",
                        tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()));

        fluxReport.doOnNext(value -> System.out.println("[subscribe] Received value: " + value))
                .blockLast();

    }
}
