package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.configurations.validator.Policies;
import com.intuit.ratelimiter.constants.RateLimitType;
import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.generator.KeyGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RateLimiterProperties {

    private String name;
    private boolean enabled;
    private String repository;
    private RateLimiterType algorithm;
    private Map<String, Policy> service = new HashMap<>();

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    @ToString
    public static class Policy{

        @NotNull
        private int refreshInterval;

        private int limit;

        @Valid
        @NotNull
        private List<MatchType> type =  new ArrayList<>();

        private Map<String, ClientPolicy> client = new HashMap<>();

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @ToString
        public static class MatchType {

            private RateLimitType type;


        }

    }

    @AllArgsConstructor
    @Data
    @ToString
    @NoArgsConstructor
    public static class ClientPolicy{

        @NotNull
        private int clientRefreshInterval;

        @NotNull
        private int clientLimit;

        @Valid
        @NotNull
        private List<RateLimitType> type =  new ArrayList<>();

    }


}
