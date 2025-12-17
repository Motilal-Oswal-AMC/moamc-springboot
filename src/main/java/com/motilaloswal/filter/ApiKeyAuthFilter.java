package com.motilaloswal.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.security.aem-key}")
    private String aemApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get the header
        String requestApiKey = request.getHeader("X-API-Key");

        // 2. Validate
        // Check for nulls to avoid NullPointerException
        if (requestApiKey != null && aemApiKey.equals(requestApiKey)) {

            // 3. Authenticate in Spring Context
            var authentication = new UsernamePasswordAuthenticationToken("aem-service", null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. Continue the chain
            filterChain.doFilter(request, response);

        } else {
            // 5. REJECT IMMEDIATELY
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("Unauthorized: Invalid API Key");
            // Do NOT call filterChain.doFilter here. We want to stop the request.
        }
    }

    // Optional: Exclude swagger or health checks from this filter if needed
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // FIX: Add your public path here so the filter skips it
        return path.startsWith("/actuator") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/api/public/v1/");
    }
}