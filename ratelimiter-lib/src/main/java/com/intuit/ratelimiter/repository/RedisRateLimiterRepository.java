package com.intuit.ratelimiter.repository;

import com.intuit.ratelimiter.model.Rate;

public class RedisRateLimiterRepository implements RateLimiterRepository{

    @Override
    public Rate consume(String key) {
        return null;
    }
}
