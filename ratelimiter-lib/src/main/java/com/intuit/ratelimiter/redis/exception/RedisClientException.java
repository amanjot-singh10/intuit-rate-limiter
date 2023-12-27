package com.intuit.ratelimiter.redis.exception;

public class RedisClientException extends Exception {

    @Override
    public String getMessage() {
        String msg = "Redis Connection has not been established !!! ";
        return msg;
    }

}
