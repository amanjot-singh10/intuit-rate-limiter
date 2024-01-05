package com.intuit.ratelimiter.filters;

import com.intuit.ratelimiter.configurations.RateLimiterProperties;
import com.intuit.ratelimiter.exception.RateNotFound;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.service.RateLimiterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

import static com.intuit.ratelimiter.constants.RateLimiterConstants.RESPONSE_CODE_DENY;


@Slf4j
public class RateLimitFilter implements Filter {

    private static final String CLIENT_HEADER = "clientId";
    private static final String SERVICE_HEADER = "serviceId";
    RateLimiterService rateLimiterService;
    RateLimiterProperties rateLimiterProperties;

    public RateLimitFilter(RateLimiterService rateLimiterService, RateLimiterProperties rateLimiterProperties) {
        this.rateLimiterService = rateLimiterService;
        this.rateLimiterProperties = rateLimiterProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String clientId = ((HttpServletRequest) request).getHeader(CLIENT_HEADER);
        String serviceId = ((HttpServletRequest) request).getHeader(SERVICE_HEADER);
        log.info("Rate Limit filter:  Request for serviceId - {}, clientId - {} ", serviceId, clientId);
        String uri = ((HttpServletRequest) request).getRequestURI();
        Rate rate = null;
        try {
            rate = rateLimiterService.consume(clientId, serviceId);
        } catch (RateNotFound e) {
            response.getOutputStream().write("Couldn't find the rate".getBytes());
            return;
        }
        if (rate.getStatus().isPermit() == 0) {
            log.warn("Request rejected for serviceId - {}, clientId - {} as limit exceeded !!", serviceId,clientId);
            response.getOutputStream().write("Too Many Requests !!".getBytes());
            return;
        }
        generateResponse(response, rate);
        chain.doFilter(request, response);
    }

    public void generateResponse(ServletResponse response, Rate rate) {
        ((HttpServletResponse) response).setStatus(RESPONSE_CODE_DENY);
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Remaining", rate.getRemaining());
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Limit", rate.getLimit());
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Retry-After", rate.getRefreshInterval());
    }

    @Override
    public void destroy() {

    }
}