package com.intuit.ratelimiter.generator;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RateLimiterProperties1;

@FunctionalInterface
public interface KeyGenerator {

    public String key(RateLimiterProperties rateLimiterProperties, String service, String clientId);

}
