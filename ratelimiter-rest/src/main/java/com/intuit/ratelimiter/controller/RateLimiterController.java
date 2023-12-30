package com.intuit.ratelimiter.controller;

import com.intuit.ratelimiter.configurations.RateProperties;
import com.intuit.ratelimiter.core.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {

    @GetMapping("/ratelimiter/")
    public String checkLimit(){
        return "Success";
    }

}
