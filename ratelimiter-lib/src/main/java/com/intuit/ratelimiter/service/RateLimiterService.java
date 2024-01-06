package com.intuit.ratelimiter.service;

import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.model.Rate;

public interface RateLimiterService {

    public Rate consume(String clientId, String service);

}
