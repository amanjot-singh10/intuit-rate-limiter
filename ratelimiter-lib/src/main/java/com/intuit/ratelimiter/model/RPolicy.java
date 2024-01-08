package com.intuit.ratelimiter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class RPolicy {

    private int limit;
    private int refreshInterval;
    private int refill;
}
