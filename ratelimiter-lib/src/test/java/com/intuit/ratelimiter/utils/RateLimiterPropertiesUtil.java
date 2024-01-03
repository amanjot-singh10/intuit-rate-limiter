package com.intuit.ratelimiter.utils;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

public class RateLimiterPropertiesUtil {

    public static RateLimiterProperties getRateLimiterProperty(String path) throws IOException {
        RateLimiterPropertiesUtil rateLimiterPropertiesTest = new RateLimiterPropertiesUtil();
        InputStream inputStream = rateLimiterPropertiesTest.getClass()
                .getClassLoader()
                .getResourceAsStream(path);
        Yaml yaml = new Yaml(new Constructor(RateLimiterProperties.class, new LoaderOptions()));
        RateLimiterProperties rateLimiterProperties = yaml.load(inputStream);
        inputStream.close();
        return rateLimiterProperties;
    }

}
