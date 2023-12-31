package com.intuit.ratelimiter.service;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.model.Rate;

public interface RateLimiterService {

    public Rate consume(String clientId, String service, RateLimiterProperties rateLimiterProperties);
    public Rate setRateAndConsume(String key, int limit, int refreshInterval);

}
