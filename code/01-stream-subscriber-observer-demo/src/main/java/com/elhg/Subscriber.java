package com.elhg;

public interface Subscriber<T> {
    void onNext(T value); // Procesa el siguiente elemento de la secuencia.
    String getName(); // Devuelve el nombre del suscriptor.
}
