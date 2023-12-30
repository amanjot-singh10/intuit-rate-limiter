package com.intuit.ratelimiter.configurations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(value = RedisProperties.PREFIX)
public class RedisProperties {

    public static final String PREFIX = "redis";

    private String redisHost;
    private int redisPort;
    private String redisPassword;
    private int redisConnectionTimeout;
    private int redisPoolMaxIdle;
    private int redisPoolMinIdle;
    private long redisPoolMaxWaitMillis;
    private int redisPoolMaxTotal;
    private String redisKeyPrefix;
    private int checkActionTimeout;

}
