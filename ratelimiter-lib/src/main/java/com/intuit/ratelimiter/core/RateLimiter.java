package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.model.Rate;

public interface RateLimiter {
        public Rate checkLimit(String key, int limit, int refreshInterval);
}
