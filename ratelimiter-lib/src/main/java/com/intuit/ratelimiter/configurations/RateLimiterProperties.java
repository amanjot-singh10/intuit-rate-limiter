package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.constants.RateLimiterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RateLimiterProperties {

    @Valid
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

        @Valid
        private int limit;

        private Map<String, ClientPolicy> client = new HashMap<>();

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

    }


}
