package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateProcessingException;
import com.intuit.ratelimiter.helper.ScriptLoader;
import com.intuit.ratelimiter.model.RPolicy;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RScript;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.codec.StringCodec;
import java.util.*;

@Slf4j
public abstract class AbstractRateLimiter implements RateLimiter{

        protected RateLimiterRedisConnection rateLimiterRedisConnection;
        protected ScriptLoader scriptLoader;
        public AbstractRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection, String scriptPath) throws FileLoadException {
                this.rateLimiterRedisConnection= rateLimiterRedisConnection;
                this.scriptLoader = new ScriptLoader(scriptPath);
        }

        public abstract Rate checkLimit(String key, RPolicy rPolicy);

        protected Rate createRate(List<Object> resp) {
                Rate rate = new Rate();
                rate.setStatus(RateLimitStatus.valueOf(((String)resp.get(0)).toUpperCase(Locale.ROOT)));
                List<Object> output = (ArrayList<Object>)resp.get(1);
                rate.setLimit(Integer.valueOf((String) output.get(0)));
                rate.setRefreshInterval(Integer.valueOf((String)output.get(1)));
                rate.setRemaining(Integer.valueOf((String)output.get(2)));
                return rate;
        }
}
