package com.example.counter.auth.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.counter.auth.jwt.JwtService;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.counter.user.UserRepository;
import com.example.counter.user.User;

import java.time.Duration;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
public class AuthController {
  private final JwtService jwt;
  private final UserRepository users;


  @PostMapping("/signup")
  public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
    var username = body.get("username").toLowerCase();
    var password = body.get("password");
    if (users.findByUsername(username).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username exists"));
    }
    var u = users.save(new User(username, password));
    return ResponseEntity.ok(Map.of("ok", true, "id", u.getUserId()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse res) {
    var user = users.findByUsername(body.get("username")).orElseThrow();

    if (!user.getPassword().equals(body.get("password"))) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid password"));
    }

    var access = jwt.generateToken(user.getUsername(), user.getUserId());
    var refresh = jwt.generateRefreshToken(user.getUsername(), user.getUserId());

    // Only set refresh token as cookie, access token will be in response body
    res.addHeader(HttpHeaders.SET_COOKIE,
        CookieUtil.refreshCookie(refresh, Duration.ofDays(1).toSeconds()).toString());

    return ResponseEntity.ok(Map.of(
        "id", user.getUserId(), 
        "username", user.getUsername(),
        "accessToken", access,
        "expiresIn", Duration.ofMinutes(30).toSeconds()));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse res) {
    // Clear the refresh token cookie
    res.addHeader(HttpHeaders.SET_COOKIE,
        CookieUtil.refreshCookie("", 0).toString());

    return ResponseEntity.ok(Map.of("ok", true, "message", "Logged out successfully"));
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) String refresh) {
    if (refresh == null){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final String username;
    try {
      username = jwt.validateAndGetSubject(refresh);
    } catch (TokenExpiredException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token expired"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
    }

    var u = users.findByUsername(username).orElseThrow();
    var access = jwt.generateToken(u.getUsername(), u.getUserId());

    return ResponseEntity.ok(Map.of(
        "ok", true,
        "username", u.getUsername(),
        "userId", u.getUserId(),
        "accessToken", access,
        "expiresIn", Duration.ofMinutes(30).toSeconds()));
  }

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser() {
    String username = com.example.counter.auth.security.CurrentUser.getCurrentUsername();
    Long userId = com.example.counter.auth.security.CurrentUser.getCurrentUserId();

    if (username == null || userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
    }

    return ResponseEntity.ok(Map.of(
        "username", username,
        "userId", userId));
  }
}
