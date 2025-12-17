package com.motilaloswal.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class RequestLogFilter implements Filter {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("RequestLogFilter: Incoming request to {} {}", ((HttpServletRequest) servletRequest).getMethod(), ((HttpServletRequest) servletRequest).getRequestURI());
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String servletPath = ((HttpServletRequest) servletRequest).getServletPath();
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            filterChain.doFilter(servletRequest, servletResponse);
            log.debug("RequestLogFilter: Completed processing for requestId: {}, Path: {}", requestId, servletPath);
        }finally {
            MDC.remove("requestId");
        }
        log.info("RequestLogFilter: Outgoing response from {} {}, Status: {}", ((HttpServletRequest) servletRequest).getMethod(), ((HttpServletRequest) servletRequest).getRequestURI(), httpServletResponse.getStatus());
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
