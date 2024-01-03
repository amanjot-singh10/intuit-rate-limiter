package com.intuit.ratelimiter.controller;

import com.intuit.ratelimiter.configurations.RateProperties;
import com.intuit.ratelimiter.core.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RateLimiterController {

    @GetMapping("/ratelimiter/limit")
    public ResponseEntity<String> consume(){
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
