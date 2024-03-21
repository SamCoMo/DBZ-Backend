package com.samcomo.dbz.member.jwt;

import com.samcomo.dbz.member.model.constants.TokenType;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static final String ID_KEY = "id";
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

  public String getId(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(ID_KEY, String.class);
  }

  public String getRole(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(ROLE_KEY, String.class);
  }

  public boolean isExpired(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getExpiration().before(new Date());
  }

  public String createToken(
      TokenType tokenType, String id, String role) {

    return Jwts.builder()
        .subject(tokenType.getKey())
        .claim(ID_KEY, id)
        .claim(ROLE_KEY, role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + tokenType.getExpiredMs()))
        .signWith(secretKey)
        .compact();
  }
  public boolean validateToken(String token){
    try{
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e){
      return false;
    }
  }
}