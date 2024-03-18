package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.entity.RefreshToken;
import com.samcomo.dbz.member.model.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

  private void rotateRefreshToken(
      Long memberId, String oldRefreshToken, String newRefreshToken) {

    refreshTokenRepository.deleteByMemberIdAndRefreshToken(memberId, oldRefreshToken);

    Date expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN.getExpiredMs());

    refreshTokenRepository.save(RefreshToken.builder()
        .memberId(memberId)
        .refreshToken(newRefreshToken)
        .expiration(String.valueOf(expiration))
        .build());
  }

  private void validateRefreshToken(String refreshToken) {

    if (refreshToken == null || !jwtUtil.getTokenType(refreshToken).equals("Refresh-Token")) {
      throw new MemberException(INVALID_REFRESH_TOKEN);
    }

    if (!refreshTokenRepository.existsByRefreshToken(refreshToken)) {
      throw new MemberException(INVALID_REFRESH_TOKEN);
    }

    try {
      jwtUtil.isExpired(refreshToken);

    } catch (ExpiredJwtException e) {
      throw new MemberException(ErrorCode.REFRESH_TOKEN_EXPIRED);
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