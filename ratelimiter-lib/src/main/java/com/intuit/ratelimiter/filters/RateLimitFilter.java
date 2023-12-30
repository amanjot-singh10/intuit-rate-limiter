package com.intuit.ratelimiter.filters;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.generator.DefaultKeyGenerator;
import com.intuit.ratelimiter.generator.KeyGenerator;
import com.intuit.ratelimiter.model.Rate;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.intuit.ratelimiter.constants.RateLimiterConstants.RESPONSE_CODE_DENY;


//TODO clean this class - removed unwanted code
@Slf4j
public class RateLimitFilter implements Filter
{
    RateLimiter rateLimiter;
    RateLimiterProperties rateLimiterProperties;
    KeyGenerator keyGenerator;

    public RateLimitFilter(RateLimiter rateLimiter, RateLimiterProperties rateLimiterProperties) {
        this.rateLimiter = rateLimiter;
        this.rateLimiterProperties=rateLimiterProperties;
        keyGenerator = new DefaultKeyGenerator();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String clientId = ((HttpServletRequest) request).getHeader("clientId");
        String serviceId = ((HttpServletRequest) request).getHeader("serviceId");
        String uri = ((HttpServletRequest) request).getRequestURI();

        System.out.println("InsideFilter =================== "+ serviceId +"  "+clientId + "   "+uri);
        String key = keyGenerator.key(rateLimiterProperties,serviceId, clientId);

        System.out.println(key);
        if(!key.isEmpty()) {
            int limit = rateLimiterProperties.getService().get(serviceId).getLimit();
            int refresh = rateLimiterProperties.getService().get(serviceId).getRefreshInterval();
            if (rateLimiterProperties.getService().containsKey(serviceId)
                    && rateLimiterProperties.getService().get(serviceId).getClient().containsKey(clientId)) {
                limit = rateLimiterProperties.getService().get(serviceId).getClient().get(clientId).getClientLimit();
                refresh = rateLimiterProperties.getService().get(serviceId).getClient().get(clientId).getClientRefreshInterval();
            }
            int a = rateLimiter.getRemainingLimit(key);
            Rate rate = rateLimiter.tryConsume(key, limit, refresh);
            generateResponse(response, rate);
            if (rate.getStatus().isPermit() == 0) {
                response.getOutputStream().write("Too Many Requests !!".getBytes());
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public  void generateResponse(ServletResponse response, Rate rate){
        ((HttpServletResponse) response).setStatus(RESPONSE_CODE_DENY);
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Remaining", rate.getRemaining());
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Limit", rate.getLimit());
    }

    @Override
    public void destroy() {

    }
}