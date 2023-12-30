package com.intuit.ratelimiter.generator;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.configurations.RateLimiterProperties1;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.StringJoiner;

public class DefaultKeyGenerator implements KeyGenerator{

    private static final String delimiter = "-";

    @Override
    public String key(RateLimiterProperties rateLimiterProperties, String service, String clientId) {
        boolean clientLimit = false;
        final StringJoiner joiner = new StringJoiner("-");
        if(Objects.isNull(service) || !rateLimiterProperties.getService().containsKey(service)){
            return joiner.toString();
        }
        joiner.add(service);
        if(rateLimiterProperties.getService().get(service).getClient().containsKey(clientId)){
            joiner.add(clientId);
        }
        return joiner.toString();
    }
}
