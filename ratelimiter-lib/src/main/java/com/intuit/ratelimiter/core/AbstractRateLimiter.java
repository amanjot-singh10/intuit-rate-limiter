package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;

//TODO improve log statements
public abstract class AbstractRateLimiter implements RateLimiter{

        protected RateLimiterProperties rateLimiterProperties;
        protected RateLimiterRedisConnection rateLimiterRedisConnection;

        public AbstractRateLimiter(RateLimiterProperties rateLimiterProperties, RateLimiterRedisConnection rateLimiterRedisConnection){
                this.rateLimiterProperties= rateLimiterProperties;
                this.rateLimiterRedisConnection= rateLimiterRedisConnection;
        }

        public abstract Rate tryConsume(String key, int limit, int rereshInterval);
        public abstract int getRemainingLimit(String key);
}
