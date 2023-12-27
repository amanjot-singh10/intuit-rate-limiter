package com.intuit.ratelimiter.core;

public interface RateLimiter {

        public String tryConsume(String key);
        public int getRemainingLimit();

}
