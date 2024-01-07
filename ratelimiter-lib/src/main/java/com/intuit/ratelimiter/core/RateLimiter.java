package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.model.Rate;


public interface RateLimiter {
        public Rate checkLimit(String key, Rate rate);
        public RateLimiterType getRateLimiterType();
}
