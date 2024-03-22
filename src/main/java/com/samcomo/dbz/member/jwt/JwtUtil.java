package com.samcomo.dbz.member.jwt;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.constants.TokenType;
import static com.samcomo.dbz.global.exception.ErrorCode.ACCESS_TOKEN_EXPIRED;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_TOKEN_TYPE;
import static com.samcomo.dbz.global.exception.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import io.jsonwebtoken.ExpiredJwtException;
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

  private JwtUtil(@Value("${spring.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(
        secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public Long getId(String token) {
    return Long.valueOf(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(ID_KEY, String.class));
  }

  public String getRole(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(ROLE_KEY, String.class);
  }

  private String getTokenType(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getSubject();
  }

  private boolean isAccessTokenType(TokenType tokenType) {
    return tokenType == ACCESS_TOKEN;
  }

  private boolean isTokenTypeCorrect(String token, TokenType tokenType) {
    return getTokenType(token).equals(tokenType.getKey());
  }

  public void validateTokenAndTokenType(String token, TokenType requiredTokenType) {
    boolean isRequiredAccessType = isAccessTokenType(requiredTokenType);

    try {
      if (!isTokenTypeCorrect(token, requiredTokenType)) {
        throw new MemberException(INVALID_TOKEN_TYPE);
      }
    } catch (ExpiredJwtException e) {
      throw new MemberException(
          isRequiredAccessType ? ACCESS_TOKEN_EXPIRED : REFRESH_TOKEN_EXPIRED);
    } catch (JwtException | IllegalArgumentException e) {
      throw new MemberException(isRequiredAccessType ? INVALID_ACCESS_TOKEN : INVALID_REFRESH_TOKEN);
    }
  }

  public String createToken(TokenType tokenType, String id, String role) {
    return Jwts.builder()
        .subject(tokenType.getKey())
        .claim(ID_KEY, id)
        .claim(ROLE_KEY, role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + tokenType.getExpiredMs()))
        .signWith(secretKey)
        .compact();
  }
}