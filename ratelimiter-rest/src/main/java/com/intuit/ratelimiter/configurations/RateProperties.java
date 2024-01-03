package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.configurations.validator.Policies;
import com.intuit.ratelimiter.constants.RateLimiterType;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
@ConfigurationProperties(value = RateProperties.PREFIX, ignoreInvalidFields = false)
public class RateProperties {

    public static final String PREFIX = "intuit.ratelimit";
    private boolean enabled;

    private String repository;

    private RateLimiterType algorithm;

    @NestedConfigurationProperty
    private Map<String, Policy> service = new HashMap<>();

    @Data
    @ToString
    public static class ClientPolicy {

        @Valid
        @Policies
        private int clientRefreshInterval = 60;
        private int clientLimit;

    }

    @Data
    @ToString
    public static class Policy {
        private int refreshInterval;
        private int limit;

        @NestedConfigurationProperty
        private Map<String, ClientPolicy> client = new HashMap<>();

        @Data
        @ToString
        public static class MatchType {
            private String type;

            public MatchType(String type) {
                this.type = type;
            }

        }
    }


}
