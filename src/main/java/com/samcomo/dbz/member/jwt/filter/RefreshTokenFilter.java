package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.samcomo.dbz.global.exception.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static com.samcomo.dbz.global.exception.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.constants.TokenType;
import com.samcomo.dbz.member.model.entity.RefreshToken;
import com.samcomo.dbz.member.model.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter {

  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;

  public void reissue(String oldRefreshToken, HttpServletResponse response) {

    validateRefreshToken(oldRefreshToken);

    String newAccessToken = getNewToken(ACCESS_TOKEN, oldRefreshToken);
    String newRefreshToken = getNewToken(REFRESH_TOKEN, oldRefreshToken);

    rotateRefreshToken(oldRefreshToken, newRefreshToken);

    response.setHeader(ACCESS_TOKEN.getKey(), newAccessToken);
    response.addCookie(createCookie(newRefreshToken));
  }

  @Transactional
  public void rotateRefreshToken(String oldRefreshToken, String newRefreshToken) {
    deleteOldRefreshTokenFromDataBase(oldRefreshToken);
    saveNewRefreshTokenToDataBase(newRefreshToken);
  }

  private void validateRefreshToken(String oldRefreshToken) {
    if (isNull(oldRefreshToken) || isTokenTypeCorrect(oldRefreshToken)) {
      throw new MemberException(INVALID_REFRESH_TOKEN);
    }

    // 탈취한 refresh1 로 새로운 refresh2 가 생성된 경우 기존 유저의 refresh1 은 DB 에 존재하지 않는다.
    if (!isTokenInDataBase(oldRefreshToken)) {
      // 탈취 가능성을 고려하여 토큰을 전부 삭제한 후 재로그인을 요청한다.
      deleteAllRefreshTokenFromDataBase(oldRefreshToken);
      throw new MemberException(REFRESH_TOKEN_NOT_FOUND);
    }

    try {
      checkExpiration(oldRefreshToken);
    } catch (ExpiredJwtException e) {
      throw new MemberException(REFRESH_TOKEN_EXPIRED);
    }
  }

  private String getNewToken(TokenType tokenType, String oldRefreshToken) {
    String memberId = jwtUtil.getId(oldRefreshToken);
    String role = jwtUtil.getRole(oldRefreshToken);
    return jwtUtil.createToken(tokenType, memberId, role);
  }

  private void deleteOldRefreshTokenFromDataBase(String oldRefreshToken) {
    Long memberId = getMemberId(oldRefreshToken);
    refreshTokenRepository.deleteByRefreshTokenAndMemberId(oldRefreshToken, memberId);
  }

  private void saveNewRefreshTokenToDataBase(String newRefreshToken) {
    Date expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN.getExpiredMs());
    refreshTokenRepository.save(RefreshToken.builder()
        .memberId(getMemberId(newRefreshToken))
        .refreshToken(newRefreshToken)
        .expiration(String.valueOf(expiration))
        .build());
  }

  private Cookie createCookie(String newRefreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN.getKey(), newRefreshToken);
    cookie.setMaxAge(24 * 60 * 60);
    // cookie.setSecure(true); // csrf 공격 방지
    cookie.setHttpOnly(true); // xss 공격 방지 (js 접근 불가)
    return cookie;
  }

  private void deleteAllRefreshTokenFromDataBase(String oldRefreshToken) {
    Long memberId = getMemberId(oldRefreshToken);
    refreshTokenRepository.deleteAllByMemberId(memberId);
  }

  public boolean isNull(String refreshToken) {
    return refreshToken == null;
  }

  public void checkExpiration(String refreshToken) throws ExpiredJwtException {
    jwtUtil.isExpired(refreshToken);
  }

  public boolean isTokenTypeCorrect(String refreshToken) {
    return !jwtUtil.getTokenType(refreshToken).equals(REFRESH_TOKEN.getKey());
  }

  public boolean isTokenInDataBase(String refreshToken) {
    Long memberId = getMemberId(refreshToken);
    return refreshTokenRepository.existsByRefreshTokenAndMemberId(refreshToken, memberId);
  }

  private Long getMemberId(String refreshToken) {
    return Long.valueOf(jwtUtil.getId(refreshToken));
  }
}