package com.intuit.ratelimiter.constants;

public class RateLimiterConstants {

    public final static int RESPONSE_CODE_DENY = 429;
    public static final String HEADER_LIMIT = "X-RateLimit-Limit";
    public static final String HEADER_REMAINING = "X-RateLimit-Remaining";
    public static final String HEADER_RESET = "X-RateLimit-Reset";

}
