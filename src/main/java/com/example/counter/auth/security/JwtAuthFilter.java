// com.example.counter.auth.security.JwtAuthFilter.java
package com.example.counter.auth.security;

import com.example.counter.auth.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.counter.auth.security.JwtAuthenticationToken;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
  
  private final JwtService jwt;
  private final AppUserDetailsService users;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws java.io.IOException, jakarta.servlet.ServletException {

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      var token = readCookie(req.getCookies(), "access_token");
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
        } catch (Exception e) {
          logger.warn("JWT authentication failed: {}", e.getMessage());
        }
      }
    }
    chain.doFilter(req, res);
  }

  private static String readCookie(Cookie[] cookies, String name) {
    if (cookies == null) return null;
    return Arrays.stream(cookies).filter(c -> name.equals(c.getName()))
        .findFirst().map(Cookie::getValue).orElse(null);
  }
}
