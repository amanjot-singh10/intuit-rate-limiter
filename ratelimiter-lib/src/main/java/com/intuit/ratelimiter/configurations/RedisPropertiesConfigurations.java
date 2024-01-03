package com.intuit.ratelimiter.configurations;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RedisPropertiesConfigurations {

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
