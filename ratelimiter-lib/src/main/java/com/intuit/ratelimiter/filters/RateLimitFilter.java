package com.intuit.ratelimiter.filters;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.service.RateLimiterService;
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
    RateLimiterService rateLimiterService;
    RateLimiterProperties rateLimiterProperties;

    public RateLimitFilter(RateLimiterService rateLimiterService, RateLimiterProperties rateLimiterProperties) {
        this.rateLimiterService = rateLimiterService;
        this.rateLimiterProperties=rateLimiterProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String clientId = ((HttpServletRequest) request).getHeader("clientId");
        String serviceId = ((HttpServletRequest) request).getHeader("serviceId");
        String uri = ((HttpServletRequest) request).getRequestURI();
        Rate rate = rateLimiterService.consume(clientId, serviceId, rateLimiterProperties);
        generateResponse(response, rate);
        if (rate.getStatus().isPermit() == 0) {
            response.getOutputStream().write("Too Many Requests !!".getBytes());
            return;
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