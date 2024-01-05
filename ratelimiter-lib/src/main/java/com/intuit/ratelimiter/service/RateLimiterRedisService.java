package com.intuit.ratelimiter.service;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.exception.RateNotFound;
import com.intuit.ratelimiter.helper.KeyMaker;
import com.intuit.ratelimiter.model.Rate;
import lombok.extern.slf4j.Slf4j;
import org.jboss.marshalling.Pair;

@Slf4j
public class RateLimiterRedisService implements RateLimiterService {

    protected RateLimiter rateLimiter;
    protected RateLimiterProperties rateLimiterProperties;
    protected KeyMaker keyMaker;

    public RateLimiterRedisService(RateLimiter rateLimiter, RateLimiterProperties rateLimiterProperties,
                                   KeyMaker keyMaker) {
        this.rateLimiter = rateLimiter;
        this.rateLimiterProperties = rateLimiterProperties;
        this.keyMaker = keyMaker;
    }

    @Override
    public Rate consume(String clientId, String serviceId) throws RateNotFound {
        String key = keyMaker.key(rateLimiterProperties, serviceId, clientId);
        if (key.isEmpty()) {
            log.info("Key generated is Blank, request rejected !!");
            return new Rate(RateLimitStatus.DENY, "0", "0", "0");
        }
        log.info("Generated Key - {}", key);
        Pair<Integer, Integer> limitPair = getPolicyLimit(rateLimiterProperties, serviceId, clientId);
        Rate rate = rateLimiter.checkLimit(key, limitPair.getA(), limitPair.getB());
        return rate;
    }

    private Pair<Integer, Integer> getPolicyLimit(RateLimiterProperties rateLimiterProperties, String serviceId, String clientId) {

        int limit = rateLimiterProperties.getService().get(serviceId).getLimit();
        int refresh = rateLimiterProperties.getService().get(serviceId).getRefreshInterval();
        if (rateLimiterProperties.getService().containsKey(serviceId)
                && rateLimiterProperties.getService().get(serviceId).getClient().containsKey(clientId)) {
            limit = rateLimiterProperties.getService().get(serviceId).getClient().get(clientId).getClientLimit();
            refresh = rateLimiterProperties.getService().get(serviceId).getClient().get(clientId).getClientRefreshInterval();
        }
        return new Pair<>(limit, refresh);
    }

}
