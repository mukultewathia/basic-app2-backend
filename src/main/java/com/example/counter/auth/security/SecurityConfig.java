// com.example.auth.security.SecurityConfig.java
package com.example.counter.auth.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Arrays;

@Configuration @EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwt, XsrfFilter xsrfFilter, CorsConfigurationSource corsSource) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // Disable CSRF for JWT-based auth
      .cors(cors -> cors.configurationSource(corsSource))
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
         .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/refresh").permitAll()
         .anyRequest().authenticated())
      .addFilterBefore(xsrfFilter, UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class)
      .formLogin(form -> form.disable()) // Disable form login
      .httpBasic(basic -> basic.disable()) // Disable HTTP basic auth
      .logout(logout -> logout.disable()); // Disable default logout

    return http.build();
  }

  @Bean PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsSource(
      @Value("${app.cors.allowed-origins}") String allowedOrigins) {
    
    var cfg = new CorsConfiguration();

    List<String> origins = Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .toList();
    
    cfg.setAllowedOrigins(origins);
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS", "DELETE"));
    cfg.setAllowedHeaders(List.of("Content-Type","X-XSRF-TOKEN","Authorization"));
     // Allow credentials (cookies, authorization headers) to be sent with cross-origin requests
    cfg.setExposedHeaders(List.of("X-XSRF-TOKEN"));
    cfg.setAllowCredentials(true);
    cfg.setMaxAge(3600L);
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
