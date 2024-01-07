package com.intuit.ratelimiter.properties;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.utils.RateLimiterPropertiesUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RateLimiterPropertiesTest {

        public static final String script = "application-ratelimit-fixed.yml";

        @Test
        public void testRateLimiterProperties() throws IOException {
            RateLimiterProperties rateLimiterProperties = RateLimiterPropertiesUtil.getRateLimiterProperty(script);

            Assertions.assertEquals(RateLimiterType.FIXED, rateLimiterProperties.getAlgorithm());
            Assertions.assertEquals(2, rateLimiterProperties.getService().size());
        }

}
