package com.intuit.ratelimiter.repository;

import com.intuit.ratelimiter.model.Rate;

public interface RateLimiterRepository {

    public Rate consume(String key);
}
