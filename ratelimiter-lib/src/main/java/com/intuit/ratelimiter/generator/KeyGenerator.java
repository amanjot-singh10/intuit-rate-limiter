package com.intuit.ratelimiter.generator;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;

@FunctionalInterface
public interface KeyGenerator {

    public String key(RateLimiterProperties rateLimiterProperties, String service, String clientId);

}
