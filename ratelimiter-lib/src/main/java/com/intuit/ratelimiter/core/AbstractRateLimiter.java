package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RateLimiterProperties1;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.utils.ScriptLoader;

public abstract class AbstractRateLimiter implements RateLimiter{

        protected RateLimiterProperties rateLimiterProperties;
        protected RateLimiterRedisConnection rateLimiterRedisConnection;
        protected ScriptLoader slidingWindowScript;
        public AbstractRateLimiter(RateLimiterProperties rateLimiterProperties){
                this(rateLimiterProperties, new RedisPropertiesConfigurations());
        }

        public AbstractRateLimiter(RateLimiterProperties rateLimiterProperties, RedisPropertiesConfigurations redisPropertiesConfigurations){
                this.rateLimiterProperties= rateLimiterProperties;
                this.rateLimiterRedisConnection= new RateLimiterRedisConnection(redisPropertiesConfigurations);
                slidingWindowScript = new ScriptLoader("scripts\\sliding-window-ratelimit.lua");
        }


        public abstract String tryConsume(String key);
        public abstract int getRemainingLimit();
}
