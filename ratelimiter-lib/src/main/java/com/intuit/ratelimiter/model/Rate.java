package com.intuit.ratelimiter.model;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Rate {

    private RateLimitStatus status;
    private String limit;
    private String refreshInterval;
    private String remaining;

}
