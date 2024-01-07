package com.intuit.ratelimiter.controller;

import com.intuit.ratelimiter.configurations.RateProperties;
import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.service.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
public class RateLimiterController {

    @Autowired
    RedisPropertiesConfigurations redisPropertiesConfigurations;

    @Autowired
    RateLimiterService rateLimiterService;


    @GetMapping("/ratelimiter/limit")
    public ResponseEntity<String> consume(){
        //rateLimiterService.consume("clientB","serviceZ");
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }


}
