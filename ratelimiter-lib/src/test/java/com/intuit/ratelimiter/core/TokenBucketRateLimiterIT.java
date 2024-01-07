package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.helper.DefaultKeyMaker;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.service.RateLimiterRedisService;
import com.intuit.ratelimiter.service.RateLimiterService;
import com.intuit.ratelimiter.utils.RateLimiterPropertiesUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

public class TokenBucketRateLimiterIT {

    private RedisPropertiesConfigurations redisPropertiesConfigurations;

    private RateLimiterRedisConnection rateLimiterRedisConnection;

    private RateLimiterService rateLimiterService;
    RateLimiterProperties rateLimiterProperties;

    public TokenBucketRateLimiterIT() throws FileLoadException, IOException {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application-redis.yml");
        Yaml yaml = new Yaml(new Constructor(RedisPropertiesConfigurations.class, new LoaderOptions()));
        redisPropertiesConfigurations = yaml.load(inputStream);
        rateLimiterProperties = RateLimiterPropertiesUtil.getRateLimiterProperty("application-ratelimit-token_bucket.yml");
        rateLimiterService = new RateLimiterRedisService(redisPropertiesConfigurations, rateLimiterProperties, new DefaultKeyMaker());
    }

    @ParameterizedTest
    @CsvSource({"testB, serviceB"})
    public void testTokenTryConsume(String clientId, String service) throws InterruptedException {

        int limit = rateLimiterProperties.getService().get(service)
                .getClient().get(clientId).getClientLimit();

        int counter= 1;
        Rate rate1 = rateLimiterService.consume(clientId, service);
        Assertions.assertEquals(limit,rate1.getLimit());

        if(limit >= counter)
            Assertions.assertEquals("ALLOW",rate1.getStatus().name());
        else
            Assertions.assertEquals("DENY",rate1.getStatus().name());
        Assertions.assertEquals(limit-counter<0 ? 0:(limit-counter),rate1.getRemaining());

        counter++;
        Rate rate2 = rateLimiterService.consume(clientId, service);
        if(limit >= counter)
            Assertions.assertEquals("ALLOW",rate2.getStatus().name());
        else
            Assertions.assertEquals("DENY",rate2.getStatus().name());
        Assertions.assertEquals(limit,rate2.getLimit());
        Assertions.assertEquals(limit-counter<0 ? 0:(limit-counter),rate2.getRemaining());

        counter++;
        Rate rate3 = rateLimiterService.consume(clientId, service);
        if(limit >= counter)
            Assertions.assertEquals("ALLOW",rate3.getStatus().name());
        else
            Assertions.assertEquals("DENY",rate3.getStatus().name());
        Assertions.assertEquals(limit,rate3.getLimit());
        Assertions.assertEquals(limit-counter<0 ? 0:(limit-counter),rate3.getRemaining());

        Thread.sleep(7000); // as the refillPeriod is 7 seconds

        counter++;
        Rate rate4 = rateLimiterService.consume(clientId, service);
        if(limit >= counter)
            Assertions.assertEquals("ALLOW",rate4.getStatus().name());
        else
            Assertions.assertEquals("DENY",rate4.getStatus().name());
        Assertions.assertEquals(limit,rate4.getLimit());
        Assertions.assertEquals(((limit-counter+rate4.getRefill())>rate4.getLimit())?rate4.getLimit()-1:limit-counter+rate4.getRefill()-1,rate4.getRemaining()); // Added refill tokens
    }
}