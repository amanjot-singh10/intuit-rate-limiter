package com.intuit.ratelimiter.exception;

public class RateProcessingException extends RuntimeException {
    public RateProcessingException(String msg) {
        super(msg);
    }

    public RateProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
