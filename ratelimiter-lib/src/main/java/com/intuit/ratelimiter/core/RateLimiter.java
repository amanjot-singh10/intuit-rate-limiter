package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.model.Rate;


public interface RateLimiter {
        public Rate checkLimit(String key, int limit, int refreshInterval);
        public RateLimiterType getRateLimiterType();
}
