package com.intuit.ratelimiter.model;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Rate {

    private RateLimitStatus status;
    private String limit;
    private String refreshInterval;
    private String remaining;

}
