package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateProcessingException;
import com.intuit.ratelimiter.helper.ScriptLoader;
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
    public Rate checkLimit(String key, Rate policy) {
        log.info("Checking Rate Limit for key - {}, limit - {}, refreshInterval - {} and refill - {}",
                key, policy.getLimit(), policy.getRefreshInterval(), policy.getRefill());
        List<Object> keys = Arrays.asList(key);
        Object[] params = new Object[]{policy.getLimit(), policy.getRefill(), policy.getRefreshInterval()};
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

    protected Rate createRate(List<Object> resp) {
        Rate rate = new Rate();
        rate.setStatus(RateLimitStatus.valueOf(((String)resp.get(0)).toUpperCase(Locale.ROOT)));
        List<Object> output = (ArrayList<Object>)resp.get(1);
        rate.setLimit(Integer.valueOf((String) output.get(0)));
        rate.setRefreshInterval(Integer.valueOf((String)output.get(1)));
        rate.setRefill(Integer.valueOf((String)output.get(2)));
        rate.setRemaining(Integer.valueOf((String)output.get(3)));
        return rate;
    }

    @Override
    public RateLimiterType getRateLimiterType() {
        return RateLimiterType.TOKEN_BUCKET;
    }

}
