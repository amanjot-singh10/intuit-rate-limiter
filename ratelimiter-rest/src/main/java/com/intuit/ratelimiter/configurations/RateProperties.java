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

@ConfigurationProperties(value = RateLimitProperties1.PREFIX, ignoreInvalidFields = false)
public class RateProperties{

    public static final String PREFIX = "aman.ratelimit";

    private boolean behindProxy;

    private boolean enabled;

    private String repository;

    @NestedConfigurationProperty
    private Map<String, Policy> service = new HashMap<>();

    public String getRepository() {
        return repository;
    }


    public Map<String, Policy> getService() {
        return service;
    }

    public void setService(Map<String, Policy> service) {
        this.service = service;
    }


    public void setRepository(String repository) {
        this.repository = repository;
    }

    public boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class ClientPolicy {
        /**
         * Refresh interval window (in seconds).
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration clientRefreshInterval = Duration.ofSeconds(60);

        private Long clientLimit;

        public Duration getClientRefreshInterval() {
            return clientRefreshInterval;
        }

        public Long getClientLimit() {
            return clientLimit;
        }

        public void setClientLimit(Long clientLimit) {
            this.clientLimit = clientLimit;
        }

        public void setClientRefreshInterval(Duration clientRefreshInterval) {
            this.clientRefreshInterval = clientRefreshInterval;
        }

        @Override
        public String toString() {
            return "ClientPolicy{" +
                    "clientRefreshInterval=" + clientRefreshInterval +
                    ", clientLimit=" + clientLimit +
                    '}';
        }
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
        private Map<String, ClientPolicy> client = new HashMap<>();

        @NestedConfigurationProperty
        private List<MatchType> type = new ArrayList<>();

        public Map<String, ClientPolicy> getClient() {
            return client;
        }

        public void setClient(Map<String, ClientPolicy> client) {
            this.client = client;
        }

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

            @Override
            public String toString() {
                return "MatchType{" +
                        "type='" + type + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Policy{" +
                    "refreshInterval=" + refreshInterval +
                    ", limit=" + limit +
                    ", quota=" + quota +
                    ", breakOnMatch=" + breakOnMatch +
                    ", client=" + client +
                    ", type=" + type +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RateProperties{" +
                "behindProxy=" + behindProxy +
                ", enabled=" + enabled +
                ", repository='" + repository + '\'' +
                ", service=" + service +
                '}';
    }
}
