package com.samcomo.dbz.member.jwt;

import static com.samcomo.dbz.global.exception.ErrorCode.TOKEN_NOT_FOUND;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.member.exception.MemberException;
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
  public void validateAccessToken(String token){
    if(token != null && !token.isEmpty()){
      throw new MemberException(TOKEN_NOT_FOUND);
    }
    try{
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    } catch (JwtException | IllegalArgumentException e){
      throw new MemberException(ErrorCode.INVALID_ACCESS_TOKEN);
    }
  }
}