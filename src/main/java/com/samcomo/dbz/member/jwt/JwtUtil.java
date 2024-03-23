package com.samcomo.dbz.member.jwt;

import static com.samcomo.dbz.global.exception.ErrorCode.ACCESS_TOKEN_EXPIRED;
import static com.samcomo.dbz.global.exception.ErrorCode.ALREADY_LOGGED_IN;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_TOKEN_TYPE;
import static com.samcomo.dbz.global.exception.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.constants.TokenType;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.dto.TokenValidationResponse;
import com.samcomo.dbz.member.model.entity.RefreshToken;
import com.samcomo.dbz.member.model.repository.RefreshTokenRepository;
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
  private final RefreshTokenRepository refreshTokenRepository;

  private JwtUtil(@Value("${spring.jwt.secret}") String secret, RefreshTokenRepository refreshTokenRepository) {
    this.secretKey = new SecretKeySpec(
        secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    this.refreshTokenRepository = refreshTokenRepository;
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

  public MemberDetails extractMemberDetailsFrom(String accessToken) {
    validateTokenAndTokenType(accessToken, ACCESS_TOKEN);

    Long memberId = getId(accessToken);
    MemberRole role = MemberRole.get(getRole(accessToken))
        .orElseThrow(() -> new MemberException(INVALID_ACCESS_TOKEN));

    return MemberDetails.of(memberId, role);
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

  public TokenValidationResponse getTokenValidationResponse(String token,TokenType requiredTokenType){
    final String VALID_TOKEN_MESSAGE = "Token is Valid";
    try {
      validateTokenAndTokenType(token, requiredTokenType);
      // 토큰이 유효한 경우
      return TokenValidationResponse.builder()
          .isValid(true)
          .isExpired(false)
          .build();
    }catch (MemberException e){
      // 만료 여부 판단
      boolean isExpired = e.getErrorCode() == ACCESS_TOKEN_EXPIRED ||
          e.getErrorCode() == REFRESH_TOKEN_EXPIRED;
      // 토큰이 만료 or 유효하지않은 경우
      return TokenValidationResponse.builder()
          .isValid(false)
          .isExpired(isExpired)
          .build();
    }
  }

  public boolean isRefreshTokenInDataBase(String refreshToken) {
    Long memberId = getId(refreshToken);
    return refreshTokenRepository.existsByRefreshTokenAndMemberId(refreshToken, memberId);
  }

  public void saveRefreshTokenToDataBase(Long memberId, String refreshToken) {
    Date expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN.getExpiredMs());
    refreshTokenRepository.save(RefreshToken.builder()
        .memberId(memberId)
        .refreshToken(refreshToken)
        .expiration(String.valueOf(expiration))
        .build());
  }

  public void deleteRefreshTokenFromDataBase(String refreshToken) {
    refreshTokenRepository.deleteByRefreshToken(refreshToken);
  }

  public void checkAlreadyLoggedIn(Long memberId) {
    if (refreshTokenRepository.existsByMemberId(memberId)) {
      throw new MemberException(ALREADY_LOGGED_IN);
    }
  }

  public void deleteAllRefreshTokenFromDataBase(String oldRefreshToken) {
    refreshTokenRepository.deleteAllByMemberId(getId(oldRefreshToken));
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