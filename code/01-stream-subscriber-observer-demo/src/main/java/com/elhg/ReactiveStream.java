package com.elhg;

import lombok.extern.java.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ReactiveStream <T>{
    private static final Logger log = Logger.getLogger(ReactiveStream.class.getName());

    private final List<Subscriber<T>> subscribers = new LinkedList<>();

    public ReactiveStream<T> subscribe(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
        log.info("[subscribe] : " + subscriber.getName());
        return this;
    }

    public void unsubscribe(Subscriber<T> subscriber) {
        subscribers.remove(subscriber);
        log.info("[unsubscribe] : " + subscriber.getName());
    }

    public void emit(T value) {
        log.info("[emit] : " + value);
        for (Subscriber<T> subscriber : subscribers) {
            subscriber.onNext(value);
        }
    }
}
