package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.generator.DefaultKeyGenerator;
import com.intuit.ratelimiter.generator.KeyGenerator;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.utils.ScriptLoader;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
public class FixedWindowRateLimiter extends AbstractRateLimiter{

    private ScriptLoader fixedWindowScript ;
    private KeyGenerator keyGenerator;

    public FixedWindowRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        super(rateLimiterRedisConnection);
        keyGenerator = new DefaultKeyGenerator();
        fixedWindowScript = new ScriptLoader("scripts\\fixed-window-ratelimit.lua");
    }

    @Override
    public Rate tryConsume(String key) {
        List<Object> keys = Arrays.asList(key);
        Object[] params = new Object[] {-1, -1, "consume"};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = script.eval(key, RScript.Mode.READ_WRITE, fixedWindowScript.getScript().get(), RScript.ReturnType.MULTI, keys, params);
        Rate rate = createRate(resp);
        return rate;
    }

    private Rate createRate(List<Object> resp) {
        Rate rate = new Rate();
        rate.setStatus(RateLimitStatus.valueOf(((String)resp.get(0)).toUpperCase(Locale.ROOT)));
        List<Object> output = (ArrayList<Object>)resp.get(1);
        rate.setRefreshInterval((String)output.get(0));
        rate.setRemaining((String)output.get(1));
        return rate;
    }

    @Override
    public Rate setRate(String key, int limit, int refreshInterval) {
        List<Object> keys = Arrays.asList(key);
        Object[] params = new Object[] {limit, refreshInterval, "setrate"};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = script.eval(key, RScript.Mode.READ_WRITE, fixedWindowScript.getScript().get(), RScript.ReturnType.MULTI, keys, params);
        Rate rate = createRate(resp);
        return rate;
    }

}
