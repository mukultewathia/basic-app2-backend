package com.example.counter.auth.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class XsrfTokenUtil {
    private static final String XSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final String XSRF_HEADER_NAME = "X-XSRF-TOKEN";
    
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Validate XSRF token by comparing cookie value with header value
     */
    public boolean validateToken(HttpServletRequest request) {
        String cookieToken = getCookieValue(request, XSRF_COOKIE_NAME);
        String headerToken = request.getHeader(XSRF_HEADER_NAME);

        if (headerToken == null || cookieToken == null) return false;

        return cookieToken.equals(headerToken);
    }

    /**
     * Create a ResponseCookie for XSRF token
     */
    public ResponseCookie createXsrfCookie() {
        return ResponseCookie.from(XSRF_COOKIE_NAME, generateToken())
                .httpOnly(false) // Must be accessible to JavaScript for header inclusion
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(-1) // Session cookie
                .build();
    }


    /**
     * Generate a new XSRF token
     */
    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Get XSRF token from cookie
     */
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
