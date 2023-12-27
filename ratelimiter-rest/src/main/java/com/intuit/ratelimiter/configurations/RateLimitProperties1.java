package com.intuit.ratelimiter.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(value = RateLimitProperties1.PREFIX, ignoreInvalidFields = true)
public class RateLimitProperties1 {

    public static final String PREFIX = "intuit.ratelimit";

    @NestedConfigurationProperty
    private List<Policy> defaultPolicyList = new ArrayList<>();

    @NestedConfigurationProperty
    private Map<String, List<Policy>> policyList = new HashMap<>();

    private boolean behindProxy;

    private boolean enabled;

    private String repository;

    public List<Policy> getPolicies(String key) {
        return policyList.getOrDefault(key, defaultPolicyList);
    }

    public List<Policy> getDefaultPolicyList() {
        return defaultPolicyList;
    }

    public void setDefaultPolicyList(List<Policy> defaultPolicyList) {
        this.defaultPolicyList = defaultPolicyList;
    }

    public Map<String, List<Policy>> getPolicyList() {
        return policyList;
    }

    public void setPolicyList(Map<String, List<Policy>> policyList) {
        this.policyList = policyList;
    }

    public boolean isBehindProxy() {
        return behindProxy;
    }

    public void setBehindProxy(boolean behindProxy) {
        this.behindProxy = behindProxy;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public static class Policy {
        /**
         * Refresh interval window (in seconds).
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration refreshInterval = Duration.ofSeconds(60);

        /**
         * Request number limit per refresh interval window.
         */
        private Long limit;

        /**
         * Request time limit per refresh interval window (in seconds).
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration quota;

        private boolean breakOnMatch;

        @NestedConfigurationProperty
        private List<MatchType> type = new ArrayList<>();

        public Duration getRefreshInterval() {
            return refreshInterval;
        }

        public void setRefreshInterval(Duration refreshInterval) {
            this.refreshInterval = refreshInterval;
        }

        public Long getLimit() {
            return limit;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }

        public Duration getQuota() {
            return quota;
        }

        public void setQuota(Duration quota) {
            this.quota = quota;
        }

        public boolean isBreakOnMatch() {
            return breakOnMatch;
        }

        public void setBreakOnMatch(boolean breakOnMatch) {
            this.breakOnMatch = breakOnMatch;
        }

        public List<MatchType> getType() {
            return type;
        }

        public void setType(List<MatchType> type) {
            this.type = type;
        }

        public static class MatchType {

            private String type;

            public MatchType( String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

        }


    }

    @Override
    public String toString() {
        return "RateLimitProperties{" +
                "defaultPolicyList=" + defaultPolicyList +
                ", policyList=" + policyList +
                ", behindProxy=" + behindProxy +
                ", enabled=" + enabled +
                ", repository='" + repository + '\'' +
                '}';
    }
}