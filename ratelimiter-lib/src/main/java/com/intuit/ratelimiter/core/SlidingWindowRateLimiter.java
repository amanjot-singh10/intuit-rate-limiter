package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateProcessingException;
import com.intuit.ratelimiter.model.RPolicy;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SlidingWindowRateLimiter extends AbstractRateLimiter{

    private static final String scriptPathSliding = "scripts\\sliding-window-ratelimit.lua";

    public SlidingWindowRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        super(rateLimiterRedisConnection, scriptPathSliding);
    }

    @Override
    public Rate checkLimit(String key, RPolicy rPolicy) {
        log.info("Checking Rate Limit for key - {}, limit - {} and refreshInterval - {}", key, rPolicy.getLimit(), rPolicy.getRefreshInterval());
        List<Object> keys = Arrays.asList(key);
        Object[] params = new Object[]{rPolicy.getLimit(), rPolicy.getRefreshInterval()};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = null;
        try {
            resp = script.eval(key, RScript.Mode.READ_WRITE, scriptLoader.getScript(), RScript.ReturnType.MULTI, keys, params);
        }catch (RuntimeException ex){
            log.error("Exception while processing Rate from Redis for {} ",key);
            throw new RateProcessingException(String.format("Exception while processing Rate from Redis for %s",key), ex);
        }
        if (Objects.isNull(resp)){
            log.error("Couldn't find the Rate in Redis for {} ",key);
            throw new RateProcessingException(String.format("Couldn't find the Rate in Redis for %s ",key));
        }
        Rate rate = createRate(resp);
        log.info("Rate Status for key - {} is {}", key, rate);
        return rate;
    }

    @Override
    public RateLimiterType getRateLimiterType() {
        return RateLimiterType.SLIDING;
    }
}
