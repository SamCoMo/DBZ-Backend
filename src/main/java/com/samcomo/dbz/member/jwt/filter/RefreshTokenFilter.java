package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.constants.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter {

  private final JwtUtil jwtUtil;

  public void reissue(String oldRefreshToken, HttpServletResponse response) {

    validateRefreshToken(oldRefreshToken);

    String newAccessToken = createNewToken(ACCESS_TOKEN, oldRefreshToken);
    String newRefreshToken = createNewToken(REFRESH_TOKEN, oldRefreshToken);

    rotateRefreshToken(oldRefreshToken, newRefreshToken);

    response.setHeader(ACCESS_TOKEN.getKey(), newAccessToken);
    response.addCookie(createCookie(newRefreshToken));
  }

  @Transactional
  public void rotateRefreshToken(String oldRefreshToken, String newRefreshToken) {
    jwtUtil.deleteRefreshTokenFromDataBase(oldRefreshToken);
    jwtUtil.saveRefreshTokenToDataBase(getMemberId(newRefreshToken), newRefreshToken);
  }

  private void validateRefreshToken(String oldRefreshToken) {

    jwtUtil.validateTokenAndTokenType(oldRefreshToken, REFRESH_TOKEN);

    // 탈취한 refresh1 로 새로운 refresh2 가 생성된 경우 기존 유저의 refresh1 은 DB 에 존재하지 않는다.
    if (!isTokenInDataBase(oldRefreshToken)) {
      // 탈취 가능성을 고려하여 토큰을 전부 삭제한 후 재로그인을 요청한다.
      jwtUtil.deleteAllRefreshTokenFromDataBase(oldRefreshToken);
      throw new MemberException(REFRESH_TOKEN_NOT_FOUND);
    }
  }

  private String createNewToken(TokenType tokenType, String oldRefreshToken) {
    String memberId = String.valueOf(jwtUtil.getId(oldRefreshToken));
    String role = jwtUtil.getRole(oldRefreshToken);
    return jwtUtil.createToken(tokenType, memberId, role);
  }

  private Cookie createCookie(String newRefreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN.getKey(), newRefreshToken);
    cookie.setMaxAge(24 * 60 * 60);
    // cookie.setSecure(true); // csrf 공격 방지
    cookie.setHttpOnly(true); // xss 공격 방지 (js 접근 불가)
    return cookie;
  }

  private boolean isTokenInDataBase(String refreshToken) {
    return jwtUtil.isRefreshTokenInDataBase(refreshToken);
  }

  private Long getMemberId(String refreshToken) {
    return jwtUtil.getId(refreshToken);
  }
}