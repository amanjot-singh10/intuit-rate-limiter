package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;

//TODO improve log statements
public abstract class AbstractRateLimiter implements RateLimiter{

        protected RateLimiterRedisConnection rateLimiterRedisConnection;

        public AbstractRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection){
                this.rateLimiterRedisConnection= rateLimiterRedisConnection;
        }

        public abstract Rate tryConsume(String key);
        public abstract Rate setRate(String key, int limit, int refreshInterval);
}
