package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RateLimiterProperties1;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;

public class FixedWindowRateLimiter extends AbstractRateLimiter{

    public FixedWindowRateLimiter(RateLimiterProperties rateLimiterProperties) {
        super(rateLimiterProperties);
    }

    public FixedWindowRateLimiter(RateLimiterProperties rateLimiterProperties, RedisPropertiesConfigurations redisPropertiesConfigurations) {
        super(rateLimiterProperties, redisPropertiesConfigurations);
    }

    @Override
    public String tryConsume(String key) {
        return "";
    }

    @Override
    public int getRemainingLimit() {
        return 0;
    }

}
