package com.intuit.ratelimiter.properties;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class RateLimiterPropertiesTest {

        public static RateLimiterProperties getRateLimiterProperty(){
            RateLimiterPropertiesTest rateLimiterPropertiesTest = new RateLimiterPropertiesTest();
            InputStream inputStream = rateLimiterPropertiesTest.getClass()
                    .getClassLoader()
                    .getResourceAsStream("application-test.yml");
            Yaml yaml = new Yaml(new Constructor(RateLimiterProperties.class, new LoaderOptions()));
            RateLimiterProperties rateLimiterProperties = yaml.load(inputStream);
            return rateLimiterProperties;
        }

}
