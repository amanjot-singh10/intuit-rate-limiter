package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.constants.RateLimiterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RateLimiterProperties {

    private boolean enabled;
    private RateLimiterType algorithm;
    private Map<String, Policy> service = new HashMap<>();

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    @ToString
    public static class Policy{

        private int refreshInterval;

        private int limit;

        private int refill;

        private Map<String, ClientPolicy> client = new HashMap<>();

    }

    @AllArgsConstructor
    @Data
    @ToString
    @NoArgsConstructor
    public static class ClientPolicy{

        private int clientRefreshInterval;
        private int clientLimit;
        private int clientRefill;
    }
}
