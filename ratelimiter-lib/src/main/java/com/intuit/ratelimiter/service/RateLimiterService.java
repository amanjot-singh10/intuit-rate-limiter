package com.intuit.ratelimiter.service;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.exception.RateNotFound;
import com.intuit.ratelimiter.model.Rate;

public interface RateLimiterService {

    public Rate consume(String clientId, String service) throws RateNotFound;

}
