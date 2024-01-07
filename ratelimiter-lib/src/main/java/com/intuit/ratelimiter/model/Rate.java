package com.intuit.ratelimiter.model;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import lombok.*;

@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Rate {

    private RateLimitStatus status;
    private int limit;
    private int refreshInterval;
    private int remaining;
    private int refill;

}
