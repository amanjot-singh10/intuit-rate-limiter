package com.intuit.ratelimiter.configurations;

import com.intuit.ratelimiter.constants.RateLimitType;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.core.SlidingWindowRateLimiter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@EnableConfigurationProperties({RateProperties.class})
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterProperties rateLimitProperties(RateProperties rateProperties){
        RateLimiterProperties rateLimiterProperties = new RateLimiterProperties();
        rateLimiterProperties.setEnabled(rateProperties.getEnabled());
        rateLimiterProperties.setRepository(rateProperties.getRepository());

        Map<String, RateProperties.Policy> map = rateProperties.getService();
        Map<String, RateLimiterProperties.Policy> mapCopy = new HashMap<>();
        for(String key : map.keySet()){
            RateProperties.Policy policy = map.get(key);
            RateLimiterProperties.Policy policyCopy = new RateLimiterProperties.Policy();
            policyCopy.setLimit(policy.getLimit());
            policyCopy.setRefreshInterval(policy.getRefreshInterval());
            List<RateProperties.Policy.MatchType> matchTypeList = policy.getType();
            List<RateLimiterProperties.Policy.MatchType> matchTypeListCopy = new ArrayList<>();
            for(RateProperties.Policy.MatchType matchType : matchTypeList){
                RateLimiterProperties.Policy.MatchType matchTypeCopy = new RateLimiterProperties.Policy.MatchType();
                matchTypeCopy.setType(RateLimitType.valueOf(matchType.getType().toUpperCase(Locale.ROOT)));
            }
            policyCopy.setType(matchTypeListCopy);

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

        System.out.println("HIiiiiiiiiiii =======================   "+rateLimiterProperties);
        return rateLimiterProperties;
    }

    @Bean
    public RateLimiter slidingWindowRateLimiter(RateLimiterProperties rateLimiterProperties){
        return new SlidingWindowRateLimiter(rateLimiterProperties);
    }
}
