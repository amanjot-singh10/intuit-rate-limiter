package com.intuit.ratelimiter.constants;

public enum RateLimiterType {

    FIXED{
        @Override
        public String getKeySuffix() {
            return RateLimiterConstants.FIXED_WINDOW_SUFFIX;
        }
    },

    SLIDING{
        @Override
        public String getKeySuffix() {
            return RateLimiterConstants.SLIDING_WINDOW_SUFFIX;
        }
    },

    TOKEN_BUCKET{
        @Override
        public String getKeySuffix() {
            return RateLimiterConstants.TOKEN_BUCKET_SUFFIX;
        }
    };

    public abstract String getKeySuffix();
}
