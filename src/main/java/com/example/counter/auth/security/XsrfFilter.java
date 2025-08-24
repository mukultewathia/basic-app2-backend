package com.example.counter.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class XsrfFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(XsrfFilter.class);
    
    private final XsrfTokenUtil xsrfTokenUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip XSRF check for GET requests and OPTIONS requests as they are not state-changing requests
        if (isGetRequest(request) || isOptionsRequest(request) || isAuthenticationEndpoint(request)) {
            if (isGetRequest(request) || isAuthenticationEndpoint(request)) {
                setXsrfTokenCookie(response);
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (!xsrfTokenUtil.validateToken(request)) {
            logger.warn("XSRF token validation failed for request: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"XSRF token validation failed\"}");
            return;
        }

        logger.info("XSRF token validation passed for request: {}", request.getRequestURI());

        setXsrfTokenCookie(response);
        
        filterChain.doFilter(request, response);
    }

    private boolean isGetRequest(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }

    private boolean isOptionsRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private boolean isAuthenticationEndpoint(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/api/auth/login") || requestURI.startsWith("/api/auth/signup") || requestURI.startsWith("/api/auth/refresh");
    }

    private void setXsrfTokenCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie", xsrfTokenUtil.createXsrfCookie().toString());
    }
}
