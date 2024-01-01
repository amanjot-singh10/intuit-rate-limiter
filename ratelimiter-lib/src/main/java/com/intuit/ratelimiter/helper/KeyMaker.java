package com.intuit.ratelimiter.helper;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;

@FunctionalInterface
public interface KeyMaker {

    public String key(RateLimiterProperties rateLimiterProperties, String service, String clientId);

}
