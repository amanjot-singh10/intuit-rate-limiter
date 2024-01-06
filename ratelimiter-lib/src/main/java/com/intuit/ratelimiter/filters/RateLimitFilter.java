package com.intuit.ratelimiter.filters;

import com.intuit.ratelimiter.exception.FileLoadException;
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

    public RateLimitFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
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
        Rate rate = rateLimiterService.consume(clientId, serviceId);
        generateHeaders(response, rate);
        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_OK);
        if (rate.getStatus().isPermit() == 0) {
            log.warn("Request rejected for serviceId - {}, clientId - {} as limit exceeded !!", serviceId,clientId);
            ((HttpServletResponse) response).setStatus(RESPONSE_CODE_DENY);
            response.getOutputStream().write("Too Many Requests !!".getBytes());
            return;
        }
        chain.doFilter(request, response);
    }

    public void generateHeaders(ServletResponse response, Rate rate) {
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Remaining", rate.getRemaining());
        ((HttpServletResponse) response).addHeader("X-Ratelimit-Limit", rate.getLimit());
        if(rate.getStatus().isPermit() == 0) {
            ((HttpServletResponse) response).addHeader("X-Ratelimit-Retry-After", rate.getRefreshInterval());
        }
    }

    @Override
    public void destroy() {

    }
}