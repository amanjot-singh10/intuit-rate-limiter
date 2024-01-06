package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlidingWindowRateLimiter extends AbstractRateLimiter{

    private static final String scriptPathSliding = "scripts\\sliding-window-ratelimit.lua";

    public SlidingWindowRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        super(rateLimiterRedisConnection, scriptPathSliding);
    }

    /*@Override
    public Rate checkLimit(String key, int limit, int refreshInterval) {
        log.info("Checking Rate Limit for key - {}, limit - {} and refreshInterval - {}", key, limit, refreshInterval);
        Object[] keys = new Object[] {key};
        Object[] params = new Object[] {limit, refreshInterval};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = null;
        try{
            resp = script.eval(key, RScript.Mode.READ_WRITE, scriptLoaderSliding.getScript(), RScript.ReturnType.MULTI, Arrays.asList(keys), params);
        }catch (RuntimeException ex){
            log.error("Exception while processing Rate from Redis for {} {} {} ",key, limit, refreshInterval);
            throw new RateProcessingException(String.format("Exception while processing Rate from Redis for %s %s %s ",key, limit, refreshInterval), ex);
        }
        if (Objects.isNull(resp)){
            log.error("Couldn't find the Rate in Redis for {} {} {} ",key, limit, refreshInterval);
            throw new RateProcessingException(String.format("Couldn't find the Rate in Redis for %s %s %s ",key, limit, refreshInterval));
        }
        Rate rate = createRate(resp);
        log.info("Rate Status for key - {} is {}", key, rate);
        return rate;
    }*/

    @Override
    public RateLimiterType getRateLimiterType() {
        return RateLimiterType.SLIDING;
    }
}
