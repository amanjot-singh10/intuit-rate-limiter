package com.intuit.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;

public class RedisClientException extends RuntimeException {

    public RedisClientException(String msg){
        super(msg);
    }
    @Override
    public String getMessage() {
        return "Redis Connection has not been established !!! ";
    }

}
