package com.intuit.ratelimiter.generator;

import com.intuit.ratelimiter.configurations.RateLimiterProperties1;

@FunctionalInterface
public interface KeyGenerator {

    public String key(String name, RateLimiterProperties1.Policy policy, String clientId);

}
