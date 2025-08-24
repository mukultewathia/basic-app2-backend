// com.example.counter.auth.security.JwtAuthFilter.java
package com.example.counter.auth.security;

import com.auth0.jwt.exceptions.*;
import com.example.counter.auth.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
  
  private final JwtService jwt;
  private final AppUserDetailsService users;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain chain)
      throws java.io.IOException, jakarta.servlet.ServletException {

    if (isAuthenticationEndpoint(req)) {
      chain.doFilter(req, res);
      return;
    } 

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      var token = extractTokenFromHeader(req);
      if (token != null) {
        try {
          // validate the token and get user context
          var userContext = jwt.validateAndGetUserContext(token);
          var user = users.loadUserByUsername(userContext.getUsername());
          var authorities = user.getAuthorities() != null ? user.getAuthorities() : Collections.emptyList();
          var auth = new JwtAuthenticationToken(user.getUsername(), userContext, (Collection<? extends GrantedAuthority>) authorities);
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          SecurityContextHolder.getContext().setAuthentication(auth);
          logger.debug("JWT authentication successful for user: {} (ID: {})", userContext.getUsername(), userContext.getUserId());
        } catch (TokenExpiredException e) {
          res.setStatus(HttpStatus.UNAUTHORIZED.value());
          res.setContentType("application/json");
          res.getWriter().write("{\"error\": \"Token expired\", \"code\": \"TOKEN_EXPIRED\"}");
          return;
        }
        catch (Exception e){
          res.setStatus(HttpStatus.UNAUTHORIZED.value());
          res.setContentType("application/json");
          res.getWriter().write("{\"error\": \"Invalid token\", \"code\": \"INVALID_TOKEN\"}");
          return;
        }
      }
    }
    chain.doFilter(req, res);
  }

  private static String extractTokenFromHeader(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

  private boolean isAuthenticationEndpoint(HttpServletRequest request) {
      String requestURI = request.getRequestURI();
      return requestURI.startsWith("/api/auth/login") || requestURI.startsWith("/api/auth/signup") || requestURI.startsWith("/api/auth/refresh");
  }
}
