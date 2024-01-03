package com.intuit.ratelimiter.utils;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public  class RedisPropertiesUtil {

            public static RateLimiterProperties getRateLimiterProperty(){
                RedisPropertiesUtil rateLimiterPropertiesTest = new RedisPropertiesUtil();
                InputStream inputStream = rateLimiterPropertiesTest.getClass()
                        .getClassLoader()
                        .getResourceAsStream("application-redis.yml");
                Yaml yaml = new Yaml(new Constructor(RedisPropertiesConfigurations.class, new LoaderOptions()));
                RateLimiterProperties rateLimiterProperties = yaml.load(inputStream);
                return rateLimiterProperties;
            }

    }
