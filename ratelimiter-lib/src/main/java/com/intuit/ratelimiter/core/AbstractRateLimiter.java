package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.helper.ScriptLoader;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;

public abstract class AbstractRateLimiter implements RateLimiter{

        protected RateLimiterRedisConnection rateLimiterRedisConnection;
//        protected ScriptLoader scriptLoader;
        public AbstractRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
                this.rateLimiterRedisConnection= rateLimiterRedisConnection;
//                this.scriptLoader = new ScriptLoader(scriptPath);
        }

        public abstract Rate checkLimit(String key, int limit, int refreshInterval);
}
