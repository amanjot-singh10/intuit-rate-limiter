package com.intuit.ratelimiter.constants;

import com.intuit.ratelimiter.configurations.RateLimiterProperties1;

public enum RateLimitType {


    ORIGIN{
        @Override
        public String key(String name, RateLimiterProperties1.Policy policy, String clientId) {
            return clientId;
        }
    },

    CLIENT{
        @Override
        public String key(String name, RateLimiterProperties1.Policy policy, String clientId) {
            return clientId;
        }
    },
    SERVICE{
        @Override
        public String key(String name, RateLimiterProperties1.Policy policy, String clientId) {
            return name;
        }
    };


    public abstract String key(String name, RateLimiterProperties1.Policy policy, String clientId);
}
