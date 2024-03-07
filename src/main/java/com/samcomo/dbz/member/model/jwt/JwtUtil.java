package com.samcomo.dbz.member.model.jwt;

import com.samcomo.dbz.member.model.constants.TokenType;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static final String EMAIL_KEY = "email";
  private static final String ROLE_KEY = "role";

  private final SecretKey secretKey;

  public JwtUtil(@Value("${spring.jwt.secret}") String secret) {

    this.secretKey = new SecretKeySpec(
        secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public String getTokenType(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getSubject();
  }

  public String getEmail(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(EMAIL_KEY, String.class);
  }

  public String getRole(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(ROLE_KEY, String.class);
  }

  public boolean isExpired(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getExpiration().before(new Date());
  }

  public String createToken(TokenType tokenType, String email, String role, Long expiredMs) {

    return Jwts.builder()
        .subject(tokenType.getKey())
        .claim(EMAIL_KEY, email)
        .claim(ROLE_KEY, role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiredMs))
        .signWith(secretKey)
        .compact();
  }
}