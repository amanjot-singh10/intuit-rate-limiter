package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
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
public abstract class AbstractRateLimiter implements RateLimiter{

        protected RateLimiterRedisConnection rateLimiterRedisConnection;
        protected ScriptLoader scriptLoader;
        public AbstractRateLimiter(RateLimiterRedisConnection rateLimiterRedisConnection, String scriptPath) throws FileLoadException {
                this.rateLimiterRedisConnection= rateLimiterRedisConnection;
                this.scriptLoader = new ScriptLoader(scriptPath);
        }

        public Rate checkLimit(String key, int limit, int refreshInterval){
                log.info("Checking Rate Limit for key - {}, limit - {} and refreshInterval - {}", key, limit, refreshInterval);
                List<Object> keys = Arrays.asList(key);
                Object[] params = new Object[]{limit, refreshInterval};
                RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
                List<Object> resp = null;
                try {
                        resp = script.eval(key, RScript.Mode.READ_WRITE, scriptLoader.getScript(), RScript.ReturnType.MULTI, keys, params);
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
        }

        protected Rate createRate(List<Object> resp) {
                Rate rate = new Rate();
                rate.setStatus(RateLimitStatus.valueOf(((String)resp.get(0)).toUpperCase(Locale.ROOT)));
                List<Object> output = (ArrayList<Object>)resp.get(1);
                rate.setLimit((String)output.get(0));
                rate.setRefreshInterval((String)output.get(1));
                rate.setRemaining((String)output.get(2));
                return rate;
        }
}
