package com.intuit.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisClientException extends Exception {

    @Override
    public String getMessage() {
        String msg = "Redis Connection has not been established !!! ";
        return msg;
    }

}
