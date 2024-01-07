package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateProcessingException;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FixedWindowRateLimiter extends AbstractRateLimiter{

    private static final String scriptPathFixed = "scripts\\fixed-window-ratelimit.lua";

    public FixedWindowRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        super(rateLimiterRedisConnection, scriptPathFixed);
    }

    @Override
    public Rate checkLimit(String key, Rate policy) {
        log.info("Checking Rate Limit for key - {}, limit - {} and refreshInterval - {}", key, policy.getLimit(), policy.getRefreshInterval());
        List<Object> keys = Arrays.asList(key);
        Object[] params = new Object[]{policy.getLimit(), policy.getRefreshInterval()};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = null;
        try {
            resp = script.eval(key, RScript.Mode.READ_WRITE, scriptLoader.getScript(), RScript.ReturnType.MULTI, keys, params);
        }catch (RuntimeException ex){
            log.error("Exception while processing Rate from Redis for {} ",key);
            throw new RateProcessingException(String.format("Exception while processing Rate from Redis for %s ",key), ex);
        }
        if (Objects.isNull(resp)){
            log.error("Couldn't find the Rate in Redis for {} ",key, policy.getLimit(), policy.getRefreshInterval());
            throw new RateProcessingException(String.format("Couldn't find the Rate in Redis for %s ",key));
        }
        Rate rate = createRate(resp);
        log.info("Rate Status for key - {} is {}", key, rate);
        return rate;
    }

    @Override
    public RateLimiterType getRateLimiterType() {
        return RateLimiterType.FIXED;
    }

}
