package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.helper.ScriptLoader;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;

import java.util.*;

@Slf4j
public class SlidingWindowRateLimiter extends AbstractRateLimiter{

    private ScriptLoader slidingWindowScript ;

    public SlidingWindowRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection) throws FileLoadException {
        super(rateLimiterRedisConnection);
        slidingWindowScript = new ScriptLoader("scripts\\sliding-window-ratelimit.lua");
    }

    @Override
    public Rate tryConsume(String key)  {
        System.out.println("==================Sliding consume=================");
        Object[] keys = new Object[] {key};
        Object[] params = new Object[] {10, 45, "consume"};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = script.eval(key, RScript.Mode.READ_WRITE, slidingWindowScript.getScript().get(), RScript.ReturnType.MULTI, Arrays.asList(keys), params);
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
        System.out.println("==================Sliding setRate=================");
        Object[] keys = new Object[] {key};
        Object[] params = new Object[] {limit, refreshInterval, "setrate"};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = script.eval(key, RScript.Mode.READ_WRITE, slidingWindowScript.getScript().get(), RScript.ReturnType.MULTI, Arrays.asList(keys), params);
        Rate rate = createRate(resp);
        return rate;
    }

}
