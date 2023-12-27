package com.intuit.ratelimiter.configurations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@ConfigurationProperties(prefix = "rate-limiter.redis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisPropertiesConfigurations {

    /**
     * Redis server host
     */
    private String redisHost = "redis://127.0.0.1";
    /**
     * Redis service port
     */
    private int redisPort = 6379;
    /**
     * Redis access password
     */
    private String redisPassword = "password";
    /**
     * Redis connection timeout
     */
    private int redisConnectionTimeout = 2000;
    /**
     * max idle connections in the pool
     */
    private int redisPoolMaxIdle = 50;
    /**
     * min idle connection in the pool
     */
    private int redisPoolMinIdle = 10;
    /**
     * the max wait milliseconds for borrowing an instance from the pool
     */
    private long redisPoolMaxWaitMillis = -1;
    /**
     * the max total instances in the pool
     */
    private int redisPoolMaxTotal = 200;
    /**
     * the redis key prefix
     */
    private String redisKeyPrefix = "#RL";

    /**
     * check action execution timeout(MILLISECONDS)
     */
    private int checkActionTimeout = 100;

}
