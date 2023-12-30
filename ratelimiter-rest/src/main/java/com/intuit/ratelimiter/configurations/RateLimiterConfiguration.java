package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.core.FixedWindowRateLimiter;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.core.SlidingWindowRateLimiter;
import com.intuit.ratelimiter.filters.RateLimitFilter;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import com.intuit.ratelimiter.exception.ScriptFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
        rateLimiterProperties.setRepository(rateProperties.getRepository());
        rateLimiterProperties.setAlgorithm(rateProperties.getAlgorithm());

        Map<String, RateProperties.Policy> map = rateProperties.getService();
        Map<String, RateLimiterProperties.Policy> mapCopy = new HashMap<>();
        for(String key : map.keySet()){
            RateProperties.Policy policy = map.get(key);
            RateLimiterProperties.Policy policyCopy = new RateLimiterProperties.Policy();
            policyCopy.setLimit(policy.getLimit());
            policyCopy.setRefreshInterval(policy.getRefreshInterval());
            List<RateProperties.Policy.MatchType> matchTypeList = policy.getType();
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
        System.out.println(rateLimiterProperties);
        return rateLimiterProperties;
    }


    @Bean
    public RateLimiterRedisConnection rateLimiterRedisConnection(RedisProperties redisProperties){
        RedisPropertiesConfigurations redisPropertiesConfigurations = RedisPropertiesConfigurations.builder().redisHost(redisProperties.getRedisHost()).
                redisPort(redisProperties.getRedisPort()).redisPassword(redisProperties.getRedisPassword())
                .redisPoolMaxIdle(redisProperties.getRedisPoolMaxIdle()).redisConnectionTimeout(redisProperties.getRedisConnectionTimeout())
                .redisPoolMinIdle(redisProperties.getRedisPoolMinIdle()).redisPoolMaxTotal(redisProperties.getRedisPoolMaxTotal())
                .redisPoolMaxWaitMillis(redisProperties.getRedisPoolMaxWaitMillis()).build();

        return new RateLimiterRedisConnection(redisPropertiesConfigurations);
    }

    @Bean(name = "rateLimiter")
    @ConditionalOnProperty(value = "intuit.ratelimit.algorithm", havingValue = "SLIDING")
    public RateLimiter slidingWindowRateLimiter(RateLimiterProperties rateLimiterProperties,
                                                RateLimiterRedisConnection rateLimiterRedisConnection) throws ScriptFoundException {
        return new SlidingWindowRateLimiter(rateLimiterProperties, rateLimiterRedisConnection);
    }

    @Bean(name = "rateLimiter")
    @ConditionalOnProperty(value = "intuit.ratelimit.algorithm", havingValue = "FIXED")
    public RateLimiter fixedWindowRateLimiter(RateLimiterProperties rateLimiterProperties,
                                              RateLimiterRedisConnection rateLimiterRedisConnection) throws ScriptFoundException {
        return new FixedWindowRateLimiter(rateLimiterProperties, rateLimiterRedisConnection);
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimiterFilter(RateLimiter rateLimiter, RateLimiterProperties rateLimiterProperties){
        FilterRegistrationBean<RateLimitFilter> registrationBean
                = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter(rateLimiter, rateLimiterProperties));
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

}
