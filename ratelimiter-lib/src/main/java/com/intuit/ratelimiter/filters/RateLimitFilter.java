package com.intuit.ratelimiter.filters;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.core.RateLimiter;
import com.intuit.ratelimiter.generator.DefaultKeyGenerator;
import com.intuit.ratelimiter.generator.KeyGenerator;
import com.intuit.ratelimiter.model.Rate;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.intuit.ratelimiter.constants.RateLimiterConstants.RESPONSE_CODE_DENY;

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
        String uri = ((HttpServletRequest) request).getRequestURI();

        System.out.println("InsideFilter =================== "+clientId + "   "+uri);
        String key = keyGenerator.key(rateLimiterProperties,"serviceA", clientId);



        int limit = rateLimiterProperties.getService().get("serviceA").getLimit();
        int refresh = rateLimiterProperties.getService().get("serviceA").getRefreshInterval();
        if(rateLimiterProperties.getService().containsKey("serviceA")
                && rateLimiterProperties.getService().get("serviceA").getClient().containsKey(clientId)){
            limit= rateLimiterProperties.getService().get("serviceA").getClient().get(clientId).getClientLimit();
            refresh= rateLimiterProperties.getService().get("serviceA").getClient().get(clientId).getClientRefreshInterval();
        }

        System.out.println(key);
        Rate valid = rateLimiter.tryConsume(key, limit, refresh);
        if(valid.getStatus().isPermit() == 1) {
            generateResponse(response);
            response.getOutputStream().write("Too Many Requests !!".getBytes());
            return;
        }

        chain.doFilter(request, response);
    }

    public  void generateResponse(ServletResponse response){
        ((HttpServletResponse) response).setStatus(RESPONSE_CODE_DENY);
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Remaining", valid.getRemaining());
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Limit", valid.getLimit());
    }

    @Override
    public void destroy() {

    }
}