package com.elhg;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.util.function.Function;
import java.util.logging.Logger;


public class SubscriberImpl<T, R> implements Subscriber<T> {
    private static final Logger log = Logger.getLogger(SubscriberImpl.class.getName());

    private final Function<T, R> mapper;
    private final String name;

    public SubscriberImpl(Function<T, R> mapper, String name) {
        this.mapper = mapper;
        this.name = name;
    }

    @Override
    public void onNext(T value) {
        final var mappedValue = mapper.apply(value);
        log.info("[onNext] : "+mappedValue);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
