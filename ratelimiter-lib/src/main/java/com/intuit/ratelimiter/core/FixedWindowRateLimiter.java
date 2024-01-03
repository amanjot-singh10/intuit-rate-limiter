package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.helper.ScriptLoader;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
public class FixedWindowRateLimiter extends AbstractRateLimiter{

    private static final String scriptPathFixed = "scripts\\fixed-window-ratelimit.lua";
    private final ScriptLoader scriptLoaderFixed;
    public FixedWindowRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        super(rateLimiterRedisConnection);
        scriptLoaderFixed = new ScriptLoader(scriptPathFixed);
    }

    @Override
    public Rate checkLimit(String key, int limit, int refreshInterval) {
        log.info("Checking Rate Limit for key - {}, limit - {} and refreshInterval - {}", key, limit, refreshInterval);
        List<Object> keys = Arrays.asList(key);
        Object[] params = new Object[] {limit, refreshInterval};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = script.eval(key, RScript.Mode.READ_WRITE, scriptLoaderFixed.getScript(), RScript.ReturnType.MULTI, keys, params);
        Rate rate = createRate(resp);
        log.info("Rate Status for key - {} is {}", key, rate);
        return rate;
    }

    public Rate createRate(List<Object> resp) {
        Rate rate = new Rate();
        rate.setStatus(RateLimitStatus.valueOf(((String)resp.get(0)).toUpperCase(Locale.ROOT)));
        List<Object> output = (ArrayList<Object>)resp.get(1);
        rate.setLimit((String)output.get(0));
        rate.setRefreshInterval((String)output.get(1));
        rate.setRemaining((String)output.get(2));
        return rate;
    }

}
