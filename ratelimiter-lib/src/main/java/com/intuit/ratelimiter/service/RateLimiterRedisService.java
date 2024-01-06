package com.intuit.ratelimiter.service;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.factory.RateLimiterFactory;
import com.intuit.ratelimiter.helper.DefaultKeyMaker;
import com.intuit.ratelimiter.helper.KeyMaker;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.jboss.marshalling.Pair;

@Slf4j
public class RateLimiterRedisService implements RateLimiterService {

    RateLimiterRedisConnection rateLimiterRedisConnection;
    private  RateLimiterProperties rateLimiterProperties;
    private KeyMaker keyMaker;
    private RateLimiterFactory  rateLimiterFactory;
    public RateLimiterRedisService(RedisPropertiesConfigurations redisPropertiesConfigurations,
                                   RateLimiterProperties rateLimiterProperties) throws FileLoadException {
        this(redisPropertiesConfigurations, rateLimiterProperties, new DefaultKeyMaker());
    }

    public RateLimiterRedisService(RedisPropertiesConfigurations redisPropertiesConfigurations,
                                   RateLimiterProperties rateLimiterProperties,
                                   KeyMaker keyMaker) throws FileLoadException {
        this.rateLimiterRedisConnection = new RateLimiterRedisConnection(redisPropertiesConfigurations);
        this.rateLimiterProperties = rateLimiterProperties;
        this.keyMaker = keyMaker;
        rateLimiterFactory = RateLimiterFactory.getInstance(rateLimiterRedisConnection);
    }

    @Override
    public Rate consume(String clientId, String serviceId){
        String key = keyMaker.key(rateLimiterProperties, serviceId, clientId);
        if (key.isEmpty()) {
            log.info("Key generated is Blank, request rejected !!");
            return new Rate(RateLimitStatus.DENY, "0", "0", "0");
        }
        log.info("Generated Key - {}", key);
        Pair<Integer, Integer> limitPair = getPolicyLimit(rateLimiterProperties, serviceId, clientId);

        RateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(rateLimiterProperties.getAlgorithm());
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
