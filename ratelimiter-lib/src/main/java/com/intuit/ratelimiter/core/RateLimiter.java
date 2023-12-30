package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.model.Rate;

public interface RateLimiter {

        public Rate tryConsume(String key, int limit, int refreshInterval);
        public int getRemainingLimit(String key);

}
