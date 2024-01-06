package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.filters.RateLimitFilter;
import com.intuit.ratelimiter.helper.DefaultKeyMaker;
import com.intuit.ratelimiter.helper.KeyMaker;
import com.intuit.ratelimiter.service.RateLimiterRedisService;
import com.intuit.ratelimiter.service.RateLimiterService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@EnableConfigurationProperties({RateProperties.class, RedisProperties.class})
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterProperties rateLimitProperties(RateProperties rateProperties){
        RateLimiterProperties rateLimiterProperties = new RateLimiterProperties();
        rateLimiterProperties.setEnabled(rateProperties.isEnabled());
        rateLimiterProperties.setAlgorithm(rateProperties.getAlgorithm());

        Map<String, RateProperties.Policy> map = rateProperties.getService();
        Map<String, RateLimiterProperties.Policy> mapCopy = new HashMap<>();
        for(String key : map.keySet()){
            RateProperties.Policy policy = map.get(key);
            RateLimiterProperties.Policy policyCopy = new RateLimiterProperties.Policy();
            policyCopy.setLimit(policy.getLimit());
            policyCopy.setRefreshInterval(policy.getRefreshInterval());
            Map<String, RateProperties.ClientPolicy> map1= policy.getClient();
            Map<String, RateLimiterProperties.ClientPolicy> map1Copy = new HashMap<>();

            for(String key1 : map1.keySet()){
                RateProperties.ClientPolicy policy1 = map1.get(key1);
                RateLimiterProperties.ClientPolicy policy1Copy = new RateLimiterProperties.ClientPolicy();
                policy1Copy.setClientLimit(policy1.getClientLimit());
                policy1Copy.setClientRefreshInterval(policy1.getClientRefreshInterval());
                map1Copy.put(key1,policy1Copy);
            }

            policyCopy.setClient(map1Copy);
            mapCopy.put(key,policyCopy);
            rateLimiterProperties.setService(mapCopy);
        }
        return rateLimiterProperties;
    }

    @Bean
    public RedisPropertiesConfigurations redisPropertiesConfigurations(RedisProperties redisProperties){
        RedisPropertiesConfigurations redisPropertiesConfigurations = new RedisPropertiesConfigurations();
        redisPropertiesConfigurations.setRedisHost(redisProperties.getRedisHost());
        redisPropertiesConfigurations.setRedisPort(redisProperties.getRedisPort());
        redisPropertiesConfigurations.setRedisPassword(redisProperties.getRedisPassword());
        redisPropertiesConfigurations.setRedisPoolMaxIdle(redisProperties.getRedisPoolMaxIdle());
        redisPropertiesConfigurations.setRedisConnectionTimeout(redisProperties.getRedisConnectionTimeout());
        redisPropertiesConfigurations.setRedisPoolMinIdle(redisProperties.getRedisPoolMinIdle());
        redisPropertiesConfigurations.setRedisPoolMaxTotal(redisProperties.getRedisPoolMaxTotal());
        redisPropertiesConfigurations.setRedisPoolMaxWaitMillis(redisProperties.getRedisPoolMaxWaitMillis());
        return  redisPropertiesConfigurations;
    }

    @Bean
    public KeyMaker keyMaker(){
        return new DefaultKeyMaker();
    }

    @Bean
    public RateLimiterService rateLimiterRedisService(RedisPropertiesConfigurations redisPropertiesConfigurations,
                                                      RateLimiterProperties rateLimiterProperties,
                                                      KeyMaker keyMaker) throws FileLoadException {
        return new RateLimiterRedisService(redisPropertiesConfigurations, rateLimiterProperties, keyMaker);
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimiterFilter(RateLimiterService rateLimiterRedisService){
        FilterRegistrationBean<RateLimitFilter> registrationBean
                = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter(rateLimiterRedisService));
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
