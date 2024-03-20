package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.samcomo.dbz.global.exception.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static com.samcomo.dbz.global.exception.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
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

  public void reissue(String refreshToken, HttpServletResponse response) {

    validateRefreshToken(refreshToken);

    String memberId = jwtUtil.getId(refreshToken);
    String role = jwtUtil.getRole(refreshToken);

    String newAccessToken = jwtUtil.createToken(ACCESS_TOKEN, memberId, role);
    String newRefreshToken = jwtUtil.createToken(ACCESS_TOKEN, memberId, role);

    rotateRefreshToken(Long.valueOf(memberId), refreshToken, newRefreshToken);

    response.setHeader(ACCESS_TOKEN.getKey(), newAccessToken);
    response.addCookie(createCookie(REFRESH_TOKEN.getKey(), newRefreshToken));
  }

  @Transactional
  public void rotateRefreshToken(
      Long memberId, String oldRefreshToken, String newRefreshToken) {

    // 기존 access, refresh 토큰은 삭제한다.
    refreshTokenRepository.deleteByRefreshTokenAndMemberId(oldRefreshToken, memberId);

    Date expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN.getExpiredMs());

    refreshTokenRepository.save(RefreshToken.builder()
        .memberId(memberId)
        .refreshToken(newRefreshToken)
        .expiration(String.valueOf(expiration))
        .build());
  }

  private void validateRefreshToken(String refreshToken) {

    if (refreshToken == null || !jwtUtil.getTokenType(refreshToken).equals(REFRESH_TOKEN.getKey())) {
      throw new MemberException(INVALID_REFRESH_TOKEN);
    }

    Long memberId = Long.valueOf(jwtUtil.getId(refreshToken));
    // 탈취한 refresh1 로 새로운 refresh2 가 생성된 경우 기존 유저의 refresh1 은 DB 에 존재하지 않는다.
    if (!refreshTokenRepository.existsByRefreshTokenAndMemberId(refreshToken, memberId)) {

      // 탈취 가능성을 고려하여 토큰을 전부 삭제한 후 재로그인을 요청한다.
      refreshTokenRepository.deleteAllByMemberId(memberId);

      throw new MemberException(REFRESH_TOKEN_NOT_FOUND);
    }

    try {
      jwtUtil.isExpired(refreshToken);

    } catch (ExpiredJwtException e) {
      throw new MemberException(REFRESH_TOKEN_EXPIRED);
    }
  }

  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60);
    // cookie.setSecure(true); csrf 공격 방지 (https 가 적용되지 않은 이미지 경로 접근 방지)
    cookie.setHttpOnly(true); // xss 공격 방지 (js 접근 불가)

    return cookie;
  }
}