package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateNotFound;
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

public class FixedWindowRateLimiterIT {

    private RedisPropertiesConfigurations redisPropertiesConfigurations;

    private RateLimiterRedisConnection rateLimiterRedisConnection;

    private RateLimiterService rateLimiterService;
    FixedWindowRateLimiter fixedWindowRateLimiter;
    RateLimiterProperties rateLimiterProperties;

    public FixedWindowRateLimiterIT() throws FileLoadException, IOException {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application-redis.yml");
        Yaml yaml = new Yaml(new Constructor(RedisPropertiesConfigurations.class, new LoaderOptions()));
        redisPropertiesConfigurations = yaml.load(inputStream);
        rateLimiterRedisConnection = new RateLimiterRedisConnection(redisPropertiesConfigurations);
        fixedWindowRateLimiter = new FixedWindowRateLimiter(rateLimiterRedisConnection);
        rateLimiterProperties = RateLimiterPropertiesUtil.getRateLimiterProperty("application-ratelimit-fixed.yml");
        rateLimiterService = new RateLimiterRedisService(fixedWindowRateLimiter, rateLimiterProperties, new DefaultKeyMaker());
    }

    @ParameterizedTest
    @CsvSource({"testA, serviceA", "testA, serviceB", "testB, serviceB"})
    public void testFixedTryConsume(String clientId, String service) throws RateNotFound {
        Rate rate1 = rateLimiterService.consume(clientId, service);
        int limit = rateLimiterProperties.getService().get(service)
                .getClient().get(clientId).getClientLimit();

        int counter= 1;
        Assertions.assertEquals(String.valueOf(limit),rate1.getLimit());

        if(limit >= counter)
            Assertions.assertEquals("ALLOW",rate1.getStatus().name());
        else
            Assertions.assertEquals("DENY",rate1.getStatus().name());
        Assertions.assertEquals(String.valueOf(limit-1),rate1.getRemaining());

        counter++;
        Rate rate2 = rateLimiterService.consume(clientId, service);
        if(limit >= counter)
            Assertions.assertEquals("ALLOW",rate2.getStatus().name());
        else
            Assertions.assertEquals("DENY",rate2.getStatus().name());
        Assertions.assertEquals(String.valueOf(limit),rate2.getLimit());
        Assertions.assertEquals(String.valueOf(limit-2),rate2.getRemaining());

        counter++;
        Rate rate3 = rateLimiterService.consume(clientId, service);
        if(limit >= counter)
            Assertions.assertEquals("ALLOW",rate3.getStatus().name());
        else
            Assertions.assertEquals("DENY",rate3.getStatus().name());
        Assertions.assertEquals(String.valueOf(limit),rate3.getLimit());
        Assertions.assertEquals((limit-counter<0 ? 0+"":(limit-counter)+""),rate3.getRemaining());

    }
}