package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.generator.DefaultKeyGenerator;
import com.intuit.ratelimiter.generator.KeyGenerator;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.exception.ScriptFoundException;
import com.intuit.ratelimiter.utils.ScriptLoader;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;

import java.util.*;

@Slf4j
public class SlidingWindowRateLimiter extends AbstractRateLimiter{

    private ScriptLoader slidingWindowScript ;
    private KeyGenerator keyGenerator;

    public SlidingWindowRateLimiter(RateLimiterProperties rateLimiterProperties, RateLimiterRedisConnection rateLimiterRedisConnection) throws ScriptFoundException {
        super(rateLimiterProperties, rateLimiterRedisConnection);
        keyGenerator = new DefaultKeyGenerator();
        slidingWindowScript = new ScriptLoader("scripts\\sliding-window-ratelimit.lua");
    }

    @Override
    public Rate tryConsume(String key, int limit, int refreshInterval)  {
        Object[] keys = new Object[] {key};
        Object[] params = new Object[] {limit, refreshInterval};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        List<Object> resp = script.eval(key, RScript.Mode.READ_WRITE, slidingWindowScript.getScript().get(), RScript.ReturnType.MULTI, Arrays.asList(keys), params);
        System.out.println(resp);
        Rate rate = createRate(resp);
        return rate;
    }

    private Rate createRate(List<Object> resp) {

        Rate rate = new Rate();

        rate.setStatus(RateLimitStatus.valueOf(((String)resp.get(0)).toUpperCase(Locale.ROOT)));
        List<Object> output = (ArrayList<Object>)resp.get(1);
        rate.setLimit((String)output.get(0));
        rate.setRefreshInterval((String)output.get(1));
        rate.setRemaining((String)output.get(2));
        return rate;
    }

    //TODO implement remaining method like Fixed Window
    @Override
    public int getRemainingLimit(String key) {
        return 0;
    }

}
