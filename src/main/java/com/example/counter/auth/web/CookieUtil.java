package com.example.counter.auth.web;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
  public static ResponseCookie accessCookie(String value, long maxAgeSeconds) {
    return ResponseCookie.from("access_token", value)
        .httpOnly(true).secure(true)
        .sameSite("Lax").path("/")
        .maxAge(maxAgeSeconds).build();
  }

  public static ResponseCookie refreshCookie(String value, long maxAgeSeconds) {
    // Constrain refresh cookie to only the refresh endpoint path
    return ResponseCookie.from("refresh_token", value)
        .httpOnly(true).secure(true)
        .sameSite("Lax").path("/")
        .maxAge(maxAgeSeconds).build();
  }

  public static ResponseCookie clear(String name, String path) {
    return ResponseCookie.from(name, "")
        .httpOnly(true).secure(true)
        .sameSite("Lax").path(path)
        .maxAge(0).build();
  }
}
