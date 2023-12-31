package com.intuit.ratelimiter.constants;

public enum RateLimitStatus {
    ALLOW(1),
    DENY(0),
    KEY_MISS(-1);

    int permit;

    RateLimitStatus(int permit) {
        this.permit = permit;
    }

    public int isPermit() {
        return permit;
    }
}
