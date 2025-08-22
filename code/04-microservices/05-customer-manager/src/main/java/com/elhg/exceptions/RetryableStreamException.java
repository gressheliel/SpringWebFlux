package com.elhg.exceptions;

public class RetryableStreamException extends RuntimeException {

    public RetryableStreamException(String message) {
        super(message);
    }
}
