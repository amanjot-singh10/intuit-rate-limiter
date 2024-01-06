package com.intuit.ratelimiter.exception;

public class UnSupportedRateLimiterAlgorithm extends RuntimeException {
    public UnSupportedRateLimiterAlgorithm(String msg) {
        super(msg);
    }
}
