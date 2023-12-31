package com.intuit.ratelimiter.service;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.generator.KeyGenerator;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateLimiterRedisService implements RateLimiterService{

    protected RateLimiter rateLimiter;
    protected RateLimiterProperties rateLimiterProperties;
    protected RateLimiterRedisConnection rateLimiterRedisConnection;
    protected KeyGenerator keyGenerator;

    public RateLimiterRedisService(RateLimiter rateLimiter, RateLimiterProperties rateLimiterProperties,
                                   RateLimiterRedisConnection rateLimiterRedisConnection, KeyGenerator keyGenerator){
        this.rateLimiter = rateLimiter;
        this.rateLimiterProperties= rateLimiterProperties;
        this.rateLimiterRedisConnection= rateLimiterRedisConnection;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public Rate consume(String clientId, String serviceId, RateLimiterProperties rateLimiterProperties) {

        String key = keyGenerator.key(rateLimiterProperties,serviceId, clientId);
        Rate rate = new Rate();
        System.out.println(key);
        if(key.isEmpty()) {
            return rate;
        }
        rate = rateLimiter.tryConsume(key);
        int limit = rateLimiterProperties.getService().get(serviceId).getLimit();
        int refresh = rateLimiterProperties.getService().get(serviceId).getRefreshInterval();
        if (rateLimiterProperties.getService().containsKey(serviceId)
                && rateLimiterProperties.getService().get(serviceId).getClient().containsKey(clientId)) {
            limit = rateLimiterProperties.getService().get(serviceId).getClient().get(clientId).getClientLimit();
            refresh = rateLimiterProperties.getService().get(serviceId).getClient().get(clientId).getClientRefreshInterval();
        }
        if(rate.getStatus().equals(RateLimitStatus.KEY_MISS)) {
            rate = setRateAndConsume(key, limit, refresh);
        }
        rate.setLimit(String.valueOf(limit));
        System.out.println(rate);
        return rate;
    }

    @Override
    public Rate setRateAndConsume(String key, int limit, int refreshInterval) {
        return rateLimiter.setRate(key, limit, refreshInterval);
    }

}
