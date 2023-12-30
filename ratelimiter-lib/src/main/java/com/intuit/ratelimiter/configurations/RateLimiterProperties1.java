package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.configurations.validator.Policies;
import com.intuit.ratelimiter.constants.RateLimitType;
import com.intuit.ratelimiter.generator.KeyGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class RateLimiterProperties1 {

    private String name;
    private boolean enabled;
    @Policies
    private Map<String, List<Policy>> policyList;

    @AllArgsConstructor
    @Data
    public static class Policy{

        @NotNull
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration refreshInterval;

        private Long limit;

        @Valid
        @NotNull
        private List<RateLimitType> type =  new ArrayList<>();

    }

}
