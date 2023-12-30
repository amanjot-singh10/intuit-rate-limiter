package com.intuit.ratelimiter.generator;

import com.intuit.ratelimiter.configurations.RateLimiterProperties1;
import org.springframework.util.StringUtils;

import java.util.StringJoiner;

public class DefaultKeyGenerator_old{

    public String key(String name, RateLimiterProperties1.Policy policy, String clientId) {
        final StringJoiner joiner = new StringJoiner(":");
        joiner.add(name);
        policy.getType().forEach(type -> {
            String key = type.key(name, policy, clientId);
            if (StringUtils.hasText(key)) {
                joiner.add(key);
            }
        });
        return joiner.toString();
    }

}
