package com.example.counter.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import com.example.counter.auth.security.UserContext;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Date;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class JwtService {
  private final Algorithm algo;
  private final long accessMinutes;
  private final long refreshDays;

  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.access-minutes}") long accessMinutes,
      @Value("${security.jwt.refresh-days}") long refreshDays) {
    this.algo = Algorithm.HMAC256(secret);
    this.accessMinutes = accessMinutes;
    this.refreshDays = refreshDays;
  }

  public String generateToken(String username, long userId) {
    Instant now = Instant.now();
    return JWT.create()
        .withSubject(username)
        .withClaim("uid", userId)            // add uid
        .withIssuedAt(Date.from(now))
        .withExpiresAt(Date.from(now.plusSeconds(accessMinutes * 60)))
        .withIssuer("my-app")
        .sign(algo);
  }

  public String generateRefreshToken(String username, long userId) {
    Instant now = Instant.now();
    return JWT.create()
        .withSubject(username)
        .withClaim("uid", userId)            // add uid
        .withClaim("type", "refresh")
        .withIssuedAt(Date.from(now))
        .withExpiresAt(Date.from(now.plusSeconds(refreshDays * 24 * 3600)))
        .withIssuer("my-app")
        .sign(algo);
  }

  public String validateAndGetSubject(String token) {
    return JWT.require(algo).withIssuer("my-app").build().verify(token).getSubject();
  }

  public Long validateAndGetUserId(String token) {
    return JWT.require(algo).withIssuer("my-app").build().verify(token).getClaim("uid").asLong();
  }

  public UserContext validateAndGetUserContext(String token) {
    var decodedJWT = JWT.require(algo).withIssuer("my-app").build().verify(token);
    String username = decodedJWT.getSubject();
    Long userId = decodedJWT.getClaim("uid").asLong();
    return UserContext.of(userId, username);
  }
}
