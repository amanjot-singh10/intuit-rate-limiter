package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.constants.RateLimiterType;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(value = RateProperties.PREFIX, ignoreInvalidFields = false)
public class RateProperties {

    public static final String PREFIX = "intuit.ratelimit";
    private boolean enabled;

    private RateLimiterType algorithm;

    @NestedConfigurationProperty
    private Map<String, Policy> service = new HashMap<>();

    @Data
    @ToString
    public static class Policy {
        private int refreshInterval;
        private int limit;
        private int refill;

        @NestedConfigurationProperty
        private Map<String, ClientPolicy> client = new HashMap<>();

    }

    @Data
    @ToString
    public static class ClientPolicy {

        private int clientRefreshInterval;
        private int clientLimit;
        private int clientRefill;

    }

}
