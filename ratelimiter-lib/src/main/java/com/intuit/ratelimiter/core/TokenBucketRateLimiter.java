package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateProcessingException;
import com.intuit.ratelimiter.helper.ScriptLoader;
import com.intuit.ratelimiter.model.RPolicy;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;

import java.util.*;

@Slf4j
public class TokenBucketRateLimiter extends AbstractRateLimiter{

    private static final String scriptPathTokenBucket = "scripts\\token-bucket-ratelimit.lua";

    public TokenBucketRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        super(rateLimiterRedisConnection, scriptPathTokenBucket);
    }

    @Override
    public Rate checkLimit(String key, RPolicy rPolicy) {
        log.info("Checking Rate Limit for key - {}, limit - {}, refreshInterval - {} and refill - {}",
                key, rPolicy.getLimit(), rPolicy.getRefreshInterval(), rPolicy.getRefill());
        List<Object> keys = Arrays.asList(key);
        Object[] params = new Object[]{rPolicy.getLimit(), rPolicy.getRefill(), rPolicy.getRefreshInterval()};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = null;
        try {
            resp = script.eval(key, RScript.Mode.READ_WRITE, scriptLoader.getScript(), RScript.ReturnType.MULTI, keys, params);
        }catch (RuntimeException ex){
            log.error("Exception while processing Rate from Redis for {} ",key);
            throw new RateProcessingException(String.format("Exception while processing Rate from Redis for %s",key), ex);
        }
        if (Objects.isNull(resp)){
            log.error("Couldn't find the Rate in Redis for {}",key);
            throw new RateProcessingException(String.format("Couldn't find the Rate in Redis for %s",key));
        }
        Rate rate = createRate(resp);
        log.info("Rate Status for key - {} is {}", key, rate);
        return rate;
    }

    @Override
    public RateLimiterType getRateLimiterType() {
        return RateLimiterType.TOKEN_BUCKET;
    }

}
