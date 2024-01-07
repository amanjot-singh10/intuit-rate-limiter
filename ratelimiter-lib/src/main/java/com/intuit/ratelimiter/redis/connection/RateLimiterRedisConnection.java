package com.intuit.ratelimiter.redis.connection;


import com.intuit.ratelimiter.configurations.RedisPropertiesConfigurations;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@Slf4j
public class RateLimiterRedisConnection {

    private RedissonClient redisClient = null;

    public RateLimiterRedisConnection(RedisPropertiesConfigurations redisPropertiesConfigurations){
        log.info("Creating Redis connection with config {}",redisPropertiesConfigurations);
        Config config = new Config();
        config.useSingleServer().setAddress(redisPropertiesConfigurations.getRedisHost()+":"+redisPropertiesConfigurations.getRedisPort())
                .setConnectionMinimumIdleSize(redisPropertiesConfigurations.getRedisPoolMinIdle())
                .setConnectionPoolSize(redisPropertiesConfigurations.getRedisPoolMaxTotal())
                .setIdleConnectionTimeout(redisPropertiesConfigurations.getRedisConnectionTimeout())
                .setConnectTimeout(redisPropertiesConfigurations.getRedisConnectionTimeout());
        redisClient = Redisson.create(config);
    }


    public RedissonClient getRedisClient() {
            return redisClient;
    }
}
