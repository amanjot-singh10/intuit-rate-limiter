package com.intuit.ratelimiter.factory;

import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.core.FixedWindowRateLimiter;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.core.SlidingWindowRateLimiter;
import com.intuit.ratelimiter.core.TokenBucketRateLimiter;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.UnSupportedRateLimiterAlgorithm;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RateLimiterFactory{

    private static volatile RateLimiterFactory instance;

    private final List<RateLimiter> rateLimiterCache = new ArrayList<>();
    private final RateLimiterRedisConnection rateLimiterRedisConnection;

    public static RateLimiterFactory getInstance(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        if (instance == null) {
            synchronized (RateLimiterFactory.class) {
                if (instance == null) {
                    instance = new RateLimiterFactory(rateLimiterRedisConnection);
                }
            }
        }
        return instance;
    }

    private RateLimiterFactory(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        this.rateLimiterRedisConnection = rateLimiterRedisConnection;
        initializeRateLimiters(rateLimiterRedisConnection);

    }

    private void initializeRateLimiters(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        rateLimiterCache.add(new FixedWindowRateLimiter(rateLimiterRedisConnection));
        rateLimiterCache.add(new SlidingWindowRateLimiter(rateLimiterRedisConnection));
        rateLimiterCache.add(new TokenBucketRateLimiter(rateLimiterRedisConnection));
    }


    public RateLimiter getRateLimiter(RateLimiterType rateLimiterType){
        return rateLimiterCache.stream()
                .filter(rateLimiter -> rateLimiter.getRateLimiterType().equals(rateLimiterType))
                .findFirst()
                .orElseThrow(() -> new UnSupportedRateLimiterAlgorithm("Unsupported rate limiting algorithm: " + rateLimiterType));
    }
}
