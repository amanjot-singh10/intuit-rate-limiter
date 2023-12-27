package com.intuit.ratelimiter.controller;

import com.intuit.ratelimiter.core.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {

    @Autowired
    RateLimiter slidingWindowRateLimiter;

    @GetMapping("/ratelimiter/")
    public String checkLimit(){

        slidingWindowRateLimiter.tryConsume("clientA");


        return "Success";
    }

}
