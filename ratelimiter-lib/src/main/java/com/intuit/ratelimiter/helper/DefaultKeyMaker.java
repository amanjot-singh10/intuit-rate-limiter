package com.intuit.ratelimiter.helper;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

@Slf4j
public class DefaultKeyMaker implements KeyMaker{

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
        joiner.add(rateLimiterProperties.getAlgorithm().name().toLowerCase(Locale.ROOT));
        return joiner.toString();
    }
}
