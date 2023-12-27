package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;

import java.util.Arrays;

public class SlidingWindowRateLimiter extends AbstractRateLimiter{

    public SlidingWindowRateLimiter(RateLimiterProperties rateLimiterProperties) {
        super(rateLimiterProperties);
    }

    public SlidingWindowRateLimiter(RateLimiterProperties rateLimiterProperties, RedisPropertiesConfigurations redisPropertiesConfigurations) {
        super(rateLimiterProperties, redisPropertiesConfigurations);
    }

    @Override
    public String tryConsume(String clientId)  {
        String key="";
        if(rateLimiterProperties.getService().get("serviceA") != null
                && rateLimiterProperties.getService().get("serviceA").getClient().containsKey(clientId)){
            key= clientId+"-"+"serviceA";
        }
        Object[] keys = new Object[] {key};
        Object[] params = new Object[] {10,1};
        RScript script = rateLimiterRedisConnection.getRedisClient().getScript(StringCodec.INSTANCE);
        for(int i =0; i<200; i++) {
            try {
                if(i==100 || i==101)
                    Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Long resp = script.eval(key, RScript.Mode.READ_WRITE, slidingWindowScript.getScript().get(), RScript.ReturnType.INTEGER, Arrays.asList(keys), params);
            ;
        }
        return "resp";
    }

    @Override
    public int getRemainingLimit() {
        return 0;
    }

    public static void main(String[] args) {
        RateLimiterProperties rateLimiterProperties = new RateLimiterProperties();
        rateLimiterProperties.setEnabled(true);
        rateLimiterProperties.setName("1");

        RateLimiter rateLimiter = new SlidingWindowRateLimiter(new RateLimiterProperties());
        String a = rateLimiter.tryConsume("key");
        System.out.println(a);
    }
}
